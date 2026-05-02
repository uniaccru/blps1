-- Только для WildFly JAAS (контейнер). Таблицы Spring / user_accounts не затрагивает.
-- Пароль учётки jaasdemo в открытом виде: jaasdemo → MD5(hex), как ожидает legacy Database login module WildFly.

CREATE TABLE IF NOT EXISTS jaas_identity (
    username VARCHAR(64) PRIMARY KEY,
    password VARCHAR(128) NOT NULL,
    roles    VARCHAR(255) NOT NULL
);

-- MD5("jaasdemo") UTF-8, hex — см. wildfly/hhflow-jaas.cli
INSERT INTO jaas_identity (username, password, roles)
VALUES ('jaasdemo', '697d3516fbb03d8f0400fe1d69f12b3e', 'JAASUser')
ON CONFLICT (username) DO NOTHING;
