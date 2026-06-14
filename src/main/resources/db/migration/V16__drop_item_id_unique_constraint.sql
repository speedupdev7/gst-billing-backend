-- =========================================================
-- Flyway Migration V16
-- Remove obsolete unique constraint on item_id only from item_opening_stock
-- =========================================================

ALTER TABLE item_opening_stock
DROP CONSTRAINT IF EXISTS item_opening_stock_item_id_key;

-- =========================================================
-- END OF MIGRATION V16
-- =========================================================
