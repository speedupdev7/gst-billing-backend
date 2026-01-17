-- =========================================================
-- Flyway Migration V5
-- Description : Audit triggers for GST Billing masters
-- =========================================================

-- Common trigger function (reuse if already exists)
create or replace function set_updated_at()
returns trigger as $$
begin
    new.updated_at = current_timestamp;
    return new;
end;
$$ language plpgsql;

-- =======================
-- UNIT MASTER
-- =======================
drop trigger if exists trg_unit_updated_at on unit_master;
create trigger trg_unit_updated_at
before update on unit_master
for each row
execute function set_updated_at();

-- =======================
-- ITEM MASTER
-- =======================
drop trigger if exists trg_item_updated_at on item_master;
create trigger trg_item_updated_at
before update on item_master
for each row
execute function set_updated_at();

-- =======================
-- CUSTOMER MASTER
-- =======================
drop trigger if exists trg_customer_updated_at on customer_master;
create trigger trg_customer_updated_at
before update on customer_master
for each row
execute function set_updated_at();

-- =======================
-- SUPPLIER MASTER
-- =======================
drop trigger if exists trg_supplier_updated_at on supplier_master;
create trigger trg_supplier_updated_at
before update on supplier_master
for each row
execute function set_updated_at();
