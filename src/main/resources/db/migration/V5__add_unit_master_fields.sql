-- =========================================================
-- Flyway Migration V5
-- Description : Add additional fields to master_unit table
-- Database    : PostgreSQL
-- =========================================================

-- Add new columns to master_unit table
alter table master_unit
add column if not exists pan varchar(10),
add column if not exists city varchar(100),
add column if not exists pin_code varchar(10),
add column if not exists bank_name varchar(200),
add column if not exists account_number varchar(20),
add column if not exists ifsc_code varchar(11);

-- =========================================================
-- END OF MIGRATION V5
-- =========================================================
