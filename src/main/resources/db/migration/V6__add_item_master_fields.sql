-- =========================================================
-- Flyway Migration V6
-- Description : Add additional fields to master_item table
-- Database    : PostgreSQL
-- =========================================================

-- Add new columns to master_item table
alter table master_item
add column if not exists item_name_details varchar(500),
add column if not exists sale_price numeric(12,2),
add column if not exists mrp numeric(12,2),
add column if not exists opening_stock integer default 0;

-- Rename price column to purchase_price (only if price column exists)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'master_item' AND column_name = 'price') THEN
        ALTER TABLE master_item RENAME COLUMN price TO purchase_price;
    END IF;
END $$;

-- =========================================================
-- END OF MIGRATION V6
-- =========================================================
