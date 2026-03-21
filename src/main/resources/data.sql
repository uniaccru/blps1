INSERT INTO vacancies (id, title, status, requires_test, employer_email)
VALUES
    (1, 'Java Backend Developer', 'ACTIVE', false, 'hr-backend@company.com'),
    (2, 'Middle QA Engineer', 'ACTIVE', true, 'hr-qa@company.com'),
    (3, 'Data Analyst', 'ARCHIVED', false, 'hr-analytics@company.com');

INSERT INTO resumes (id, candidate_id, full_name, summary, created_at)
VALUES
    (1, 101, 'Иван Петров', '5 лет Java/Spring, PostgreSQL, Kafka', now()),
    (2, 102, 'Мария Соколова', '4 года QA, Postman, Playwright, SQL', now());

SELECT setval('vacancies_id_seq', (SELECT MAX(id) FROM vacancies));
SELECT setval('resumes_id_seq', (SELECT MAX(id) FROM resumes));
