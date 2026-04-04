-- =========================================================
-- Flyway Migration V6
-- Description : Add additional fields to item_master table
-- Database    : PostgreSQL
-- =========================================================

-- Add new columns to item_master table
alter table item_master
add column if not exists item_name_details varchar(500),
add column if not exists sale_price numeric(12,2),
add column if not exists mrp numeric(12,2),
add column if not exists opening_stock integer default 0;

-- Rename price column to purchase_price
alter table item_master rename column price to purchase_price;

-- =========================================================
-- END OF MIGRATION V6
-- =========================================================
