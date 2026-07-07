USE embrace_mind_care;

-- 如果你的 appointments 已經有 cancelled_at，這行可能會顯示欄位已存在，略過即可。
-- 若執行失敗，可以只執行下面 CREATE TABLE users 與 INSERT。
ALTER TABLE appointments ADD COLUMN cancelled_at DATETIME NULL;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    display_name VARCHAR(50) NOT NULL,
    professional_id INT NULL,
    active TINYINT NOT NULL DEFAULT 1,
    FOREIGN KEY (professional_id) REFERENCES professionals(id)
);

INSERT INTO users(username, password, role, display_name, professional_id, active)
SELECT 'admin', 'admin123', 'ADMIN', '系統管理員', NULL, 1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');


INSERT INTO users(username, password, role, display_name, professional_id, active)
SELECT 'doctor', '1234', 'PROFESSIONAL', '醫師共用登入帳號', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'doctor');

INSERT INTO users(username, password, role, display_name, professional_id, active)
SELECT 'psychologist', '1234', 'PROFESSIONAL', '心理師共用登入帳號', 10, 1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'psychologist');

INSERT INTO users(username, password, role, display_name, professional_id, active)
SELECT 'doctor1', 'doctor123', 'PROFESSIONAL', '陳志文醫師', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'doctor1');

INSERT INTO users(username, password, role, display_name, professional_id, active)
SELECT 'psychologist1', 'psych123', 'PROFESSIONAL', '李佳蓉臨床心理師', 10, 1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'psychologist1');

INSERT INTO users(username, password, role, display_name, professional_id, active)
SELECT 'user1', 'user123', 'USER', '一般使用者', NULL, 1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'user1');
