-- =========================================================
-- Flyway Migration V2
-- Description : Audit trigger to auto-update updated_at
-- Database    : PostgreSQL
-- =========================================================

-- =======================
-- AUDIT FUNCTION
-- =======================
create or replace function fn_set_updated_at()
returns trigger as
$$
begin
    new.updated_at = current_timestamp;
    return new;
end;
$$ language plpgsql;

-- =======================
-- ROLE MASTER TRIGGER
-- =======================
create trigger trg_role_master_updated_at
before update on role_master
for each row
execute function fn_set_updated_at();

-- =======================
-- DESIGNATION MASTER TRIGGER
-- =======================
create trigger trg_designation_master_updated_at
before update on designation_master
for each row
execute function fn_set_updated_at();

-- =======================
-- QUALIFICATION MASTER TRIGGER
-- =======================
create trigger trg_qualification_master_updated_at
before update on qualification_master
for each row
execute function fn_set_updated_at();

-- =======================
-- CITY MASTER TRIGGER
-- =======================
create trigger trg_city_master_updated_at
before update on city_master
for each row
execute function fn_set_updated_at();

-- =======================
-- EXPENSES MASTER TRIGGER
-- =======================
create trigger trg_expenses_master_updated_at
before update on expenses_master
for each row
execute function fn_set_updated_at();

-- =======================
-- EMPLOYEE MASTER TRIGGER
-- =======================
create trigger trg_employee_master_updated_at
before update on employee_master
for each row
execute function fn_set_updated_at();

-- =========================================================
-- END OF MIGRATION V2
-- =========================================================
