-- 初期管理者アカウント (password: admin123)
-- BCrypt hash of 'admin123'
INSERT INTO employees (employee_code, name, email, password, role)
VALUES ('admin', '管理者', 'admin@example.com', '$2a$10$00vp7tbeiERhS1g9KuZbh.rTxt9KOS8r69KNYX2anwE/P9p6IsHmq', 'HR');
