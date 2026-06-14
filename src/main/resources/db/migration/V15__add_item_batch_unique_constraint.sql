-- =========================================================
-- Flyway Migration V15
-- Add unique constraint for item_id + batch_code on item_opening_stock
-- =========================================================

ALTER TABLE item_opening_stock
ADD CONSTRAINT uq_item_opening_stock_item_batch UNIQUE (item_id, batch_code);

-- =========================================================
-- END OF MIGRATION V15
-- =========================================================
