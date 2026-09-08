#!/usr/bin/env bash
# Проверка Camunda + hh-flow worker через REST API Camunda.
# Предусловия: docker compose up (app:8080, camunda:8081).
#
# Сидовые данные (data.sql):
#   applicant user id=4, resume id=1, vacancy id=1 (ACTIVE, no test)
#   vacancy id=3 (ARCHIVED), vacancy id=2 (requires test)

set -euo pipefail

CAMUNDA_URL="${CAMUNDA_URL:-http://localhost:8081/engine-rest}"
APP_URL="${APP_URL:-http://localhost:8080}"

json_field() {
  python3 -c "import json,sys; d=json.load(sys.stdin); print(d$1)"
}

wait_for_process() {
  local pid="$1"
  local timeout="${2:-15}"
  local i=0
  while [ "$i" -lt "$timeout" ]; do
    local state
    state="$(curl -fsS "$CAMUNDA_URL/history/process-instance/$pid" | json_field "['state']")"
    if [ "$state" = "COMPLETED" ] || [ "$state" = "EXTERNALLY_TERMINATED" ]; then
      echo "$state"
      return 0
    fi
    # Check if waiting at a user task (active, not completed).
    local user_task
    user_task="$(curl -fsS "$CAMUNDA_URL/task?processInstanceId=$pid" 2>/dev/null | python3 -c "import json,sys; tasks=json.load(sys.stdin); print(tasks[0].get('taskDefinitionKey', tasks[0].get('name', '')) if tasks else '')" 2>/dev/null || echo "")"
    if [ -n "$user_task" ]; then
      echo "ACTIVE:$user_task"
      return 0
    fi
    sleep 1
    i=$((i + 1))
  done
  echo "TIMEOUT"
  return 1
}

echo "=== Camunda engine health ==="
curl -fsS "$CAMUNDA_URL/engine" | head -c 200
echo "..."

echo "=== Deployed process definitions ==="
curl -fsS "$CAMUNDA_URL/process-definition" | python3 -c "
import json,sys
for p in json.load(sys.stdin):
    print(f\"  {p['key']} (id={p['id']}, version={p['version']})\")
"

echo "=== hh-flow app health ==="
curl -fsS -o /dev/null -w "HTTP %{http_code}\n" "$APP_URL/api/v1/vacancies"

echo
echo "=== Scenario 1: Happy path (vacancy=1, resume=1, applicant=4) ==="
RESP="$(curl -fsS -X POST "$CAMUNDA_URL/process-definition/key/Process_JobApplication/start" \
  -H 'Content-Type: application/json' \
  -d '{
    "variables": {
      "applicantUserId": {"value": 4, "type": "Long"},
      "vacancyId": {"value": 1, "type": "Long"},
      "resumeId": {"value": 1, "type": "Long"}
    }
  }')"
PID="$(echo "$RESP" | json_field "['id']")"
echo "processInstanceId=$PID"
sleep 3
STATE="$(wait_for_process "$PID" 30 || true)"
echo "state=$STATE"
echo "Variables:"
curl -fsS "$CAMUNDA_URL/history/variable-instance?processInstanceId=$PID" | python3 -c "
import json,sys
for v in json.load(sys.stdin):
    print(f\"  {v['name']}={v['value']}\")
"
echo "External tasks (finished):"
curl -fsS "$CAMUNDA_URL/history/external-task-log?processInstanceId=$PID" | python3 -c "
import json,sys
for t in json.load(sys.stdin):
  if t.get('successLog'):
    print(f\"  topic={t.get('topicName')} activity={t.get('activityId')}\")
"

echo
echo "=== Scenario 2: Archived vacancy (vacancy=3) ==="
RESP2="$(curl -fsS -X POST "$CAMUNDA_URL/process-definition/key/Process_JobApplication/start" \
  -H 'Content-Type: application/json' \
  -d '{
    "variables": {
      "applicantUserId": {"value": 4, "type": "Long"},
      "vacancyId": {"value": 3, "type": "Long"},
      "resumeId": {"value": 1, "type": "Long"}
    }
  }')"
PID2="$(echo "$RESP2" | json_field "['id']")"
echo "processInstanceId=$PID2"
sleep 3
curl -fsS "$CAMUNDA_URL/history/variable-instance?processInstanceId=$PID2" | python3 -c "
import json,sys
for v in json.load(sys.stdin):
    print(f\"  {v['name']}={v['value']}\")
"

echo
echo "=== Scenario 3: Create vacancy process (employer=1) ==="
echo "Note: stops at Task_FillVacancy (user task) unless completed via Tasklist"
RESP3="$(curl -fsS -X POST "$CAMUNDA_URL/process-definition/key/Process_CreateVacancy/start" \
  -H 'Content-Type: application/json' \
  -d '{
    "variables": {
      "employerUserId": {"value": 1, "type": "Long"}
    }
  }')"
PID3="$(echo "$RESP3" | json_field "['id']")"
echo "processInstanceId=$PID3"
sleep 2
curl -fsS "$CAMUNDA_URL/task?processInstanceId=$PID3" | python3 -c "
import json,sys
tasks=json.load(sys.stdin)
for t in tasks:
    print(f\"  userTask: {t['name']} (id={t['id']})\")
if not tasks:
    print('  (no active user tasks)')
"

echo
echo "Готово. Cockpit: http://localhost:8081/camunda/app/cockpit/ (demo/demo)"
