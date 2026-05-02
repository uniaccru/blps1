#!/usr/bin/env bash
# Примеры запросов после появления JWT/RBAC.
# Сидовые пользователи (data.sql): пароль везде «password», телефоны см. ниже.
#
# Перед запуском: приложение на localhost:8080, БД с применённым data.sql.
# Токены берутся из ответа login (нужен python3 для разбора JSON).

set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
EMPLOYER_PHONE="${EMPLOYER_PHONE:-+10000000001}"
APPLICANT_PHONE="${APPLICANT_PHONE:-+10000000101}"
PASSWORD="${PASSWORD:-password}"

json_token() {
  python3 -c "import json,sys; print(json.load(sys.stdin)['accessToken'])"
}

echo "=== Login employer ($EMPLOYER_PHONE) ==="
EMPLOYER_TOKEN="$(curl -fsS -X POST "$BASE_URL/api/v1/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"username\":\"$EMPLOYER_PHONE\",\"password\":\"$PASSWORD\"}" | json_token)"
echo "employer_token (первые 32 символа): ${EMPLOYER_TOKEN:0:32}..."

echo "=== Login applicant ($APPLICANT_PHONE) ==="
APPLICANT_TOKEN="$(curl -fsS -X POST "$BASE_URL/api/v1/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"username\":\"$APPLICANT_PHONE\",\"password\":\"$PASSWORD\"}" | json_token)"
echo "applicant_token (первые 32 символа): ${APPLICANT_TOKEN:0:32}..."

echo "=== GET vacancies (без JWT) ==="
curl -fsS "$BASE_URL/api/v1/vacancies" | head -c 400
echo "..."

echo "=== POST vacancy (employer) ==="
curl -fsS -X POST "$BASE_URL/api/v1/vacancies" \
  -H "Authorization: Bearer $EMPLOYER_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"title":"Vacancy from curl","requiresTest":false}'
echo

echo "=== GET applications (employer, только свои вакансии) ==="
curl -fsS "$BASE_URL/api/v1/applications" \
  -H "Authorization: Bearer $EMPLOYER_TOKEN" | head -c 500
echo "..."

echo "=== GET resumes (applicant) ==="
curl -fsS "$BASE_URL/api/v1/resumes" \
  -H "Authorization: Bearer $APPLICANT_TOKEN" | head -c 400
echo "..."

echo "=== POST submit без JWT → ожидается 401 ==="
curl -sS -w "\nHTTP %{http_code}\n" -X POST "$BASE_URL/api/v1/applications/submit" \
  -H 'Content-Type: application/json' \
  -d '{"vacancyId":1,"resumeId":1}'
echo

echo "=== POST submit с JWT (vacancy 1, resume 1) ==="
curl -fsS -X POST "$BASE_URL/api/v1/applications/submit" \
  -H "Authorization: Bearer $APPLICANT_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"vacancyId":1,"resumeId":1}'
echo

echo "Готово."
