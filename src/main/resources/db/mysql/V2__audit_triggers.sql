-- =========================================================
-- Flyway Migration V2
-- Description : Audit trigger to auto-update updated_at
-- Database    : MySQL
-- =========================================================

-- =======================
-- ROLE MASTER TRIGGER
-- =======================
DELIMITER $$
CREATE TRIGGER trg_role_master_updated_at
BEFORE UPDATE ON role_master
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$
DELIMITER ;

-- =======================
-- DESIGNATION MASTER TRIGGER
-- =======================
DELIMITER $$
CREATE TRIGGER trg_designation_master_updated_at
BEFORE UPDATE ON designation_master
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$
DELIMITER ;

-- =======================
-- QUALIFICATION MASTER TRIGGER
-- =======================
DELIMITER $$
CREATE TRIGGER trg_qualification_master_updated_at
BEFORE UPDATE ON qualification_master
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$
DELIMITER ;

-- =======================
-- CITY MASTER TRIGGER
-- =======================
DELIMITER $$
CREATE TRIGGER trg_city_master_updated_at
BEFORE UPDATE ON city_master
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$
DELIMITER ;

-- =======================
-- EXPENSES MASTER TRIGGER
-- =======================
DELIMITER $$
CREATE TRIGGER trg_expenses_master_updated_at
BEFORE UPDATE ON expenses_master
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$
DELIMITER ;

-- =======================
-- EMPLOYEE MASTER TRIGGER
-- =======================
DELIMITER $$
CREATE TRIGGER trg_employee_master_updated_at
BEFORE UPDATE ON employee_master
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$
DELIMITER ;

-- =========================================================
-- END OF MIGRATION V2
-- =========================================================