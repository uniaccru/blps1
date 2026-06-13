#!/usr/bin/env bash
# E2E-проверка цепочки: submit → outbox → RabbitMQ → JMS listener → email.
#
# Требования: docker compose up (app + postgres + rabbitmq + mailhog).
# По умолчанию письмо уходит на email работодателя вакансии из data.sql:
#   vacancy 1 → hr-backend@company.com (MailHog перехватывает любой адрес).
#
# Запуск:
#   ./scripts/test-email-flow.sh
#
# Свой адрес получателя (если зарегистрировали работодателя с другим email):
#   EXPECTED_TO=stolnaya.stella@gmail.com VACANCY_ID=4 ./scripts/test-email-flow.sh
#
# Реальный Gmail вместо MailHog — см. docker-compose.gmail.yml и .env.gmail.example

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
MAILHOG_URL="${MAILHOG_URL:-http://localhost:8025}"
APPLICANT_EMAIL="${APPLICANT_EMAIL:-ivan.petrov@example.com}"
PASSWORD="${PASSWORD:-password}"
VACANCY_ID="${VACANCY_ID:-1}"
RESUME_ID="${RESUME_ID:-1}"
EXPECTED_TO="${EXPECTED_TO:-hr-backend@company.com}"
OUTBOX_WAIT_SEC="${OUTBOX_WAIT_SEC:-12}"
POLL_INTERVAL_SEC="${POLL_INTERVAL_SEC:-2}"

if ! command -v python3 >/dev/null 2>&1; then
  echo "ERROR: нужен python3 для разбора JSON" >&2
  exit 1
fi

json_get() {
  local expr="$1"
  python3 -c "import json,sys; data=json.load(sys.stdin); print($expr)"
}

echo "=== 1. Проверка доступности API ($BASE_URL) ==="
curl -fsS "$BASE_URL/api/v1/vacancies" >/dev/null
echo "OK"

echo "=== 2. Очистка MailHog (чистый прогон) ==="
curl -fsS -X DELETE "$MAILHOG_URL/api/v1/messages" >/dev/null || {
  echo "WARN: MailHog недоступен на $MAILHOG_URL — для Gmail-теста это нормально, скрипт не проверит inbox"
  SKIP_MAILHOG=1
}
SKIP_MAILHOG="${SKIP_MAILHOG:-0}"

echo "=== 3. Login applicant ($APPLICANT_EMAIL) ==="
APPLICANT_TOKEN="$(curl -fsS -X POST "$BASE_URL/api/v1/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$APPLICANT_EMAIL\",\"password\":\"$PASSWORD\"}" | json_get "data['accessToken']")"
echo "token получен"

echo "=== 4. POST /applications/submit (vacancy=$VACANCY_ID) ==="
SUBMIT_RESPONSE="$(curl -fsS -X POST "$BASE_URL/api/v1/applications/submit" \
  -H "Authorization: Bearer $APPLICANT_TOKEN" \
  -H 'Content-Type: application/json' \
  -d "{\"vacancyId\":$VACANCY_ID,\"resumeId\":$RESUME_ID}")"

echo "$SUBMIT_RESPONSE" | python3 -m json.tool

OUTCOME="$(printf '%s' "$SUBMIT_RESPONSE" | json_get "data['outcome']")"
APPLICATION_ID="$(printf '%s' "$SUBMIT_RESPONSE" | json_get "data['application']['id']")"

if [[ "$OUTCOME" != "COMPLETED" ]]; then
  echo "ERROR: ожидался outcome=COMPLETED, получен: $OUTCOME" >&2
  exit 1
fi
echo "Отклик создан: applicationId=$APPLICATION_ID"

if [[ "$SKIP_MAILHOG" == "1" ]]; then
  echo ""
  echo "Проверьте inbox $EXPECTED_TO вручную (Gmail / другой SMTP)."
  exit 0
fi

echo "=== 5. Ожидание outbox relay + JMS consumer (до ${OUTBOX_WAIT_SEC}s) ==="
DEADLINE=$(( $(date +%s) + OUTBOX_WAIT_SEC ))
FOUND=0

while [[ $(date +%s) -lt $DEADLINE ]]; do
  MESSAGES="$(curl -fsS "$MAILHOG_URL/api/v2/messages")"
  MATCH="$(printf '%s' "$MESSAGES" | EXPECTED_TO="$EXPECTED_TO" APPLICATION_ID="$APPLICATION_ID" python3 -c "
import json, os, sys
data = json.load(sys.stdin)
expected_to = os.environ['EXPECTED_TO'].lower()
app_id = os.environ['APPLICATION_ID']
for item in data.get('items', []):
    headers = item.get('Content', {}).get('Headers', {})
    to_list = [t.lower() for t in headers.get('To', [])]
    subject = ' '.join(headers.get('Subject', []))
    body = item.get('Content', {}).get('Body', '')
    if any(expected_to in t for t in to_list):
        if app_id in subject or app_id in body or 'New application' in subject:
            print('OK')
            print('To:', ', '.join(headers.get('To', [])))
            print('Subject:', subject)
            sys.exit(0)
sys.exit(1)
" 2>/dev/null || true)"

  if [[ -n "$MATCH" ]]; then
    echo "$MATCH"
    FOUND=1
    break
  fi
  sleep "$POLL_INTERVAL_SEC"
done

if [[ "$FOUND" != "1" ]]; then
  echo "ERROR: письмо для $EXPECTED_TO (applicationId=$APPLICATION_ID) не найдено в MailHog." >&2
  echo "Откройте UI: $MAILHOG_URL" >&2
  echo "Логи приложения: docker logs hh-flow-app --tail 80" >&2
  exit 1
fi

echo ""
echo "SUCCESS: email-flow работает."
echo "MailHog UI: $MAILHOG_URL"
