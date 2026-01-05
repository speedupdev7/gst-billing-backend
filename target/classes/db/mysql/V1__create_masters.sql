-- =========================================================
-- Flyway Migration V1
-- Description : Create master tables with soft delete
-- Database    : MySQL
-- =========================================================

-- =======================
-- 1. ROLE MASTER
-- =======================
CREATE TABLE IF NOT EXISTS role_master (
    role_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code       VARCHAR(20) NOT NULL UNIQUE,
    role_name       VARCHAR(100) NOT NULL,
    description     TEXT,
    is_system_role  TINYINT(1) DEFAULT 0,
    is_active       TINYINT(1) DEFAULT 1,

    is_deleted      TINYINT(1) DEFAULT 0,
    deleted_at      TIMESTAMP NULL,

    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =======================
-- 2. DESIGNATION MASTER
-- =======================
CREATE TABLE IF NOT EXISTS designation_master (
    designation_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    designation_code VARCHAR(20) NOT NULL UNIQUE,
    designation_name VARCHAR(100) NOT NULL,
    description      TEXT,
    is_active        TINYINT(1) DEFAULT 1,

    is_deleted       TINYINT(1) DEFAULT 0,
    deleted_at       TIMESTAMP NULL,

    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =======================
-- 3. QUALIFICATION MASTER
-- =======================
CREATE TABLE IF NOT EXISTS qualification_master (
    qualification_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    qualification_code VARCHAR(20) NOT NULL UNIQUE,
    qualification_name VARCHAR(100) NOT NULL,
    description        TEXT,
    is_active          TINYINT(1) DEFAULT 1,

    is_deleted         TINYINT(1) DEFAULT 0,
    deleted_at         TIMESTAMP NULL,

    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =======================
-- 4. CITY MASTER
-- =======================
CREATE TABLE IF NOT EXISTS city_master (
    city_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    city_code   VARCHAR(20) NOT NULL UNIQUE,
    city_name   VARCHAR(100) NOT NULL,
    state_name  VARCHAR(100),
    country     VARCHAR(100),
    is_active   TINYINT(1) DEFAULT 1,

    is_deleted  TINYINT(1) DEFAULT 0,
    deleted_at  TIMESTAMP NULL,

    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =======================
-- 5. EXPENSES MASTER
-- =======================
CREATE TABLE IF NOT EXISTS expenses_master (
    expense_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_code    VARCHAR(20) NOT NULL UNIQUE,
    expense_name    VARCHAR(100) NOT NULL,
    description     TEXT,
    is_reimbursable TINYINT(1) DEFAULT 1,
    is_active       TINYINT(1) DEFAULT 1,

    is_deleted      TINYINT(1) DEFAULT 0,
    deleted_at      TIMESTAMP NULL,

    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =======================
-- 6. EMPLOYEE MASTER
-- =======================
CREATE TABLE IF NOT EXISTS employee_master (
    employee_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code     VARCHAR(20) NOT NULL UNIQUE,
    first_name        VARCHAR(100) NOT NULL,
    middle_name       VARCHAR(100),
    last_name         VARCHAR(100) NOT NULL,
    gender            VARCHAR(10),
    date_of_birth     DATE,
    joining_date      DATE,
    mobile_no         VARCHAR(15),
    email_id          VARCHAR(150) UNIQUE,
    pan_number        VARCHAR(20),
    aadhaar_number    VARCHAR(20),
    address           TEXT,

    designation_id    BIGINT NOT NULL,
    qualification_id  BIGINT,
    city_id           BIGINT,
    role_id           BIGINT NOT NULL,

    is_active         TINYINT(1) DEFAULT 1,
    is_deleted        TINYINT(1) DEFAULT 0,
    deleted_at        TIMESTAMP NULL,

    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_employee_designation
        FOREIGN KEY (designation_id)
        REFERENCES designation_master (designation_id),

    CONSTRAINT fk_employee_qualification
        FOREIGN KEY (qualification_id)
        REFERENCES qualification_master (qualification_id),

    CONSTRAINT fk_employee_city
        FOREIGN KEY (city_id)
        REFERENCES city_master (city_id),

    CONSTRAINT fk_employee_role
        FOREIGN KEY (role_id)
        REFERENCES role_master (role_id)
);

-- =========================================================
-- INDEXES FOR PERFORMANCE
-- =========================================================

-- Foreign key indexes
CREATE INDEX idx_employee_designation_id ON employee_master (designation_id);
CREATE INDEX idx_employee_qualification_id ON employee_master (qualification_id);
CREATE INDEX idx_employee_city_id ON employee_master (city_id);
CREATE INDEX idx_employee_role_id ON employee_master (role_id);

-- MySQL does not support partial indexes with WHERE clauses.
-- Alternative: Use generated columns or filter in queries.
-- Example: Add composite index for active+not_deleted
CREATE INDEX idx_role_active_not_deleted ON role_master (is_active, is_deleted);
CREATE INDEX idx_designation_active_not_deleted ON designation_master (is_active, is_deleted);
CREATE INDEX idx_qualification_active_not_deleted ON qualification_master (is_active, is_deleted);
CREATE INDEX idx_city_active_not_deleted ON city_master (is_active, is_deleted);
CREATE INDEX idx_expenses_active_not_deleted ON expenses_master (is_active, is_deleted);
CREATE INDEX idx_employee_active_not_deleted ON employee_master (is_active, is_deleted);

-- =========================================================
-- END OF MIGRATION V1
-- =========================================================