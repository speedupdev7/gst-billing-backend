-- =========================================================
-- Flyway Migration V10
-- Description : Ensure invoice backend support columns and tables
-- Database    : PostgreSQL
-- =========================================================

alter table if exists invoice_item add column if not exists batch_code varchar(20);
alter table if exists invoice_item add column if not exists is_active boolean default true;
alter table if exists invoice_payment add column if not exists is_active boolean default true;
alter table if exists invoice_balance add column if not exists is_active boolean default true;
alter table if exists gst_adjustment_note add column if not exists is_active boolean default true;

-- The invoice tables are created in V3__create_billing_tables.sql.
-- This migration ensures invoice tables have the BaseMasterEntity "is_active" column and batch support.
