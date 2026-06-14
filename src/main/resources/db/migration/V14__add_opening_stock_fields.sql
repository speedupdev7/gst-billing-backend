-- =========================================================
-- Flyway Migration V14
-- Add additional fields to item_opening_stock table
-- =========================================================

alter table item_opening_stock 
add column if not exists supplier_name varchar(255),
add column if not exists remarks text,
add column if not exists expiry_date date;

-- =========================================================
-- END OF MIGRATION V14
-- =========================================================
