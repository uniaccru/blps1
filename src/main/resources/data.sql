INSERT INTO employers (id, email)
VALUES
    (1, 'hr-backend@company.com'),
    (2, 'hr-qa@company.com'),
    (3, 'hr-analytics@company.com');

INSERT INTO applicants (id)
VALUES
    (101),
    (102);

-- password for all seeded accounts: password (BCrypt)
INSERT INTO user_accounts (phone, password_hash, role, employer_id, applicant_id)
VALUES
    ('+10000000001', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'EMPLOYER', 1, NULL),
    ('+10000000002', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'EMPLOYER', 2, NULL),
    ('+10000000003', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'EMPLOYER', 3, NULL),
    ('+10000000101', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'APPLICANT', NULL, 101),
    ('+10000000102', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'APPLICANT', NULL, 102);

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
SELECT setval(pg_get_serial_sequence('applicants', 'id'), (SELECT MAX(id) FROM applicants));

SELECT setval('vacancies_id_seq', (SELECT MAX(id) FROM vacancies));
SELECT setval('resumes_id_seq', (SELECT MAX(id) FROM resumes));
SELECT setval(pg_get_serial_sequence('user_accounts', 'id'), (SELECT MAX(id) FROM user_accounts));
