-- password for all seeded accounts: password (BCrypt)
INSERT INTO user_accounts (id, phone, password_hash, role, email)
VALUES
    (1, NULL, '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'EMPLOYER', 'hr-backend@company.com'),
    (2, NULL, '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'EMPLOYER', 'hr-qa@company.com'),
    (3, NULL, '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'EMPLOYER', 'hr-analytics@company.com'),
    (4, NULL, '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'APPLICANT', 'ivan.petrov@example.com'),
    (5, NULL, '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'APPLICANT', 'maria.sokolova@example.com');

INSERT INTO vacancies (id, title, status, requires_test, employer_id)
VALUES
    (1, 'Java Backend Developer', 'ACTIVE', false, 1),
    (2, 'Middle QA Engineer', 'ACTIVE', true, 2),
    (3, 'Data Analyst', 'ARCHIVED', false, 3);

INSERT INTO resumes (id, user_id, full_name, summary, created_at)
VALUES
    (1, 4, 'Иван Петров', '5 лет Java/Spring, PostgreSQL, Kafka', now()),
    (2, 5, 'Мария Соколова', '4 года QA, Postman, Playwright, SQL', now());

SELECT setval(pg_get_serial_sequence('user_accounts', 'id'), (SELECT MAX(id) FROM user_accounts));
SELECT setval('vacancies_id_seq', (SELECT MAX(id) FROM vacancies));
SELECT setval('resumes_id_seq', (SELECT MAX(id) FROM resumes));
