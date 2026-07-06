CREATE DATABASE IF NOT EXISTS embrace_mind_care
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE embrace_mind_care;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS professionals;
DROP TABLE IF EXISTS service_types;
DROP TABLE IF EXISTS counseling_fees;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- 1. 服務類型表
-- =========================
CREATE TABLE service_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    main_category VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- =========================
-- 2. 專業人員表
-- =========================
CREATE TABLE professionals (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL,
    service_type_id INT NOT NULL,
    FOREIGN KEY (service_type_id) REFERENCES service_types(id)
);

-- =========================
-- 3. 預約時段表
-- =========================
CREATE TABLE schedules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    professional_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    session VARCHAR(20) NOT NULL,
    appointment_type VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    quota INT NOT NULL DEFAULT 0,
    booked_count INT NOT NULL DEFAULT 0,
    active TINYINT NOT NULL DEFAULT 1,
    FOREIGN KEY (professional_id) REFERENCES professionals(id)
);

-- =========================
-- 4. 個案 / 病患資料表
-- =========================
CREATE TABLE patients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    id_no VARCHAR(20) NOT NULL,
    birth_date DATE NOT NULL,
    phone VARCHAR(20) NOT NULL
);

-- =========================
-- 5. 預約紀錄表
-- =========================
CREATE TABLE appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    schedule_id INT NOT NULL,
    appointment_no INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT '預約完成',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (schedule_id) REFERENCES schedules(id)
);

-- =========================
-- 6. 諮商費用表
-- =========================
CREATE TABLE counseling_fees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    service_name VARCHAR(100) NOT NULL,
    duration_minutes INT NOT NULL,
    fee_min INT NOT NULL,
    fee_max INT NOT NULL
);

USE embrace_mind_care;

-- =========================
-- 1. 服務類型資料
-- =========================
INSERT INTO service_types(id, main_category, name, description) VALUES
(1, '醫院精神科門診', '高齡心智醫學中心', '高齡記憶、情緒與心智功能相關門診'),
(2, '醫院精神科門診', '社區精神醫療服務', '社區追蹤、復健與個案管理服務'),
(3, '醫院精神科門診', '成癮醫學發展中心', '酒精、藥物、網路與行為成癮相關服務'),
(4, '醫院精神科門診', '藥癮醫療示範中心', '藥物使用困擾與藥癮治療追蹤'),
(5, '醫院精神科門診', '腦刺激治療中心', 'rTMS、TMS 腦刺激治療相關服務'),
(6, '醫院精神科門診', '兒童青少年精神醫學中心', '兒童與青少年情緒、注意力、人際與發展議題'),
(7, '醫院精神科門診', '司法精神醫學中心', '司法相關精神醫學評估與諮詢'),
(8, '醫院精神科門診', '心身醫學中心', '壓力、自律神經與身心症狀相關服務'),
(9, '醫院精神科門診', '睡眠中心', '睡眠困擾、失眠與睡眠品質相關服務'),
(10, '心理諮商 / 心理治療', '心理諮商', '成人、青少年、兒童、伴侶與家庭諮商'),
(11, '心理諮商 / 心理治療', '心理治療', '個別心理治療與長期心理治療服務'),
(12, '臨床心理衡鑑', '臨床心理衡鑑', '智力、注意力、情緒、人格與兒童發展衡鑑'),
(13, '臨床心理衡鑑', '職能治療', '針對發展遲緩、自閉症或過動症兒童，透過遊戲與感覺統合訓練改善學習與社交功能');

-- =========================
-- 2. 專業人員資料
-- =========================
INSERT INTO professionals(id, code, name, role, service_type_id) VALUES
(1, 'P001', '陳志文', '精神科醫師', 8),
(2, 'P002', '王心婷', '精神科醫師', 6),
(3, 'P003', '林慧安', '精神科醫師', 1),
(4, 'P004', '鄭仁一', '精神科醫師', 3),
(5, 'P005', '周安安', '精神科醫師', 9),
(6, 'P006', '蔡明杰', '精神科醫師', 5),
(7, 'P007', '吳藥平', '精神科醫師', 4),
(8, 'P008', '許正倫', '精神科醫師', 7),
(9, 'P009', '謝雅琪', '精神科醫師', 2),
(10, 'C001', '李佳蓉', '臨床心理師', 12),
(11, 'C002', '黃怡君', '諮商心理師', 10),
(12, 'C003', '張心柔', '臨床心理師', 11),
(13, 'O001', '許明倫', '職能治療師', 13);

-- =========================
-- 3. 預約時段資料
-- =========================
INSERT INTO schedules
(professional_id, appointment_date, session, appointment_type, location, start_time, end_time, quota, booked_count, active)
VALUES
(1,  '2026-07-08', '上午', '心身醫學中心', '201診', '09:00:00', '12:00:00', 20, 3, 1),
(2,  '2026-07-08', '上午', '兒童青少年精神醫學中心', '202診', '09:00:00', '12:00:00', 15, 4, 1),
(3,  '2026-07-08', '下午', '高齡心智醫學中心', '203診', '14:00:00', '16:00:00', 12, 5, 1),
(11, '2026-07-08', '下午', '心理諮商', '諮商室B', '14:00:00', '14:50:00', 6, 2, 1),
(10, '2026-07-08', '下午', '臨床心理衡鑑', '衡鑑室A', '14:00:00', '16:00:00', 3, 1, 1),

(4,  '2026-07-09', '上午', '成癮醫學發展中心', '204診', '09:00:00', '12:00:00', 12, 6, 1),
(6,  '2026-07-09', '下午', '腦刺激治療中心', '治療室1', '14:00:00', '14:30:00', 10, 5, 1),
(13, '2026-07-09', '下午', '職能治療', '兒童職能治療室', '15:00:00', '15:50:00', 4, 1, 1),

(5,  '2026-07-10', '上午', '睡眠中心', '205診', '09:00:00', '12:00:00', 20, 8, 1),
(9,  '2026-07-10', '上午', '社區精神醫療服務', '社區門診', '10:00:00', '12:00:00', 12, 3, 1),
(12, '2026-07-10', '下午', '心理治療', '治療室2', '14:00:00', '14:50:00', 6, 3, 1),
(8,  '2026-07-10', '下午', '司法精神醫學中心', '206診', '15:00:00', '17:00:00', 8, 2, 1),

(7,  '2026-07-11', '上午', '藥癮醫療示範中心', '207診', '09:00:00', '12:00:00', 12, 4, 1),
(11, '2026-07-11', '下午', '心理諮商', '諮商室B', '14:00:00', '15:20:00', 4, 1, 1),
(13, '2026-07-11', '下午', '職能治療', '兒童職能治療室', '15:30:00', '16:20:00', 4, 2, 1),

(2,  '2026-07-12', '上午', '兒童青少年精神醫學中心', '202診', '09:00:00', '12:00:00', 15, 7, 1),
(10, '2026-07-12', '下午', '臨床心理衡鑑', '衡鑑室A', '14:00:00', '16:00:00', 3, 2, 1),
(6,  '2026-07-12', '下午', '腦刺激治療中心', '治療室1', '15:00:00', '15:30:00', 10, 6, 1),

(1,  '2026-07-13', '上午', '心身醫學中心', '201診', '09:00:00', '12:00:00', 20, 7, 1),
(4,  '2026-07-13', '下午', '成癮醫學發展中心', '204診', '14:00:00', '16:00:00', 12, 5, 1),
(13, '2026-07-13', '下午', '職能治療', '兒童職能治療室', '14:00:00', '14:50:00', 4, 1, 1);

-- =========================
-- 4. 諮商費用資料
-- =========================
INSERT INTO counseling_fees(service_name, duration_minutes, fee_min, fee_max) VALUES
('個別諮商（成人/青少年）', 50, 2200, 3000),
('兒童個別諮商', 50, 2200, 3000),
('伴侶諮商', 80, 3500, 5200);