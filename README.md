# hh-flow

Spring Boot сервис, реализующий бизнес-процесс отклика кандидата на вакансию.

## Что реализовано

- Layered архитектура: `controller`, `service`, `repository`, `dto`, `exception`, `config`.
- PostgreSQL для хранения данных.
- Публичные REST API для вакансий, резюме и откликов.
- Подпроцессы:
  - создание резюме — минимальный подпроцесс с созданием резюме из JSON-данных;
    - прохождение теста — заглушка с управляемым результатом.
- Тестовые данные загружаются автоматически через `data.sql`.

## Запуск

1. Поднять PostgreSQL и приложение:

```bash
docker compose up --build -d
```

2. Если нужно запускать приложение локально без Docker:

```bash
./mvnw spring-boot:run
```

или

```bash
mvn spring-boot:run
```

## Основные API

- `GET /api/v1/vacancies`
- `POST /api/v1/vacancies`
- `PATCH /api/v1/vacancies/{id}/status`
- `GET /api/v1/resumes`
- `POST /api/v1/resumes`
- `POST /api/v1/applications/submit`
- `GET /api/v1/applications`

## Сценарий отклика

`POST /api/v1/applications/submit`

Тело запроса:

```json
{
  "vacancyId": 2,
  "candidateId": 101,
  "resumeId": 1,
  "resumeFullName": "Иван Петров",
  "resumeSummary": "Java/Spring, PostgreSQL, Kafka",
  "simulateResumeCreationSuccess": true,
  "simulateTestPassed": true
}
```

Логика для `POST /api/v1/applications/submit`:
- если `resumeId` передан — используется существующее резюме;
- если `resumeId` не передан и у кандидата уже есть резюме — используется оно;
- если резюме нет, запускается подпроцесс создания резюме по полям `resumeFullName` и `resumeSummary`;
- `simulateResumeCreationSuccess=false` принудительно роняет подпроцесс создания резюме;
- `simulateTestPassed` управляет тестовым подпроцессом (для вакансий с `requiresTest=true`).

## Insomnia

Файл экспорта: `insomnia_export.json`
