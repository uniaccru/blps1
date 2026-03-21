INSERT INTO employers (id, email)
VALUES
    (1, 'hr-backend@company.com'),
    (2, 'hr-qa@company.com'),
    (3, 'hr-analytics@company.com');

INSERT INTO applicants (id)
VALUES
    (101),
    (102);

INSERT INTO vacancies (id, title, status, requires_test, employer_id)
VALUES
    (1, 'Java Backend Developer', 'ACTIVE', false, 1),
    (2, 'Middle QA Engineer', 'ACTIVE', true, 2),
    (3, 'Data Analyst', 'ARCHIVED', false, 3);

INSERT INTO resumes (id, applicant_id, full_name, summary, created_at)
VALUES
    (1, 101, 'Иван Петров', '5 лет Java/Spring, PostgreSQL, Kafka', now()),
    (2, 102, 'Мария Соколова', '4 года QA, Postman, Playwright, SQL', now());

SELECT setval('employers_id_seq', (SELECT MAX(id) FROM employers));

SELECT setval('vacancies_id_seq', (SELECT MAX(id) FROM vacancies));
SELECT setval('resumes_id_seq', (SELECT MAX(id) FROM resumes));
