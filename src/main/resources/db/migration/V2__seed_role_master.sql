-- =========================================================
-- Flyway Migration V2
-- Description : Seed data for Role Master
-- Database    : PostgreSQL
-- Note        : Inserts standard roles with permissions
-- =========================================================

-- ======================
-- ROLE MASTER SEED DATA
-- ======================
insert into role_master (role_code, role_name, description, is_system_role, is_active)
values
('ADMIN', 'Administrator', 'Full system access with all permissions', true, true),
('SUPER_ADMIN', 'Super Administrator', 'System admin with override capabilities', true, true),
('MANAGER', 'Manager', 'Manager role with limited administrative access', false, true),
('USER', 'User', 'Standard user with basic access permissions', false, true),
('GUEST', 'Guest', 'Guest role with minimal read-only access', false, true),
('ACCOUNTS', 'Accounts', 'Accounts/Finance management role', false, true),
('BILLING', 'Billing', 'Billing and invoicing role', false, true),
('INVENTORY', 'Inventory', 'Inventory management role', false, true),
('HR', 'HR', 'Human Resources management role', false, true),
('AUDITOR', 'Auditor', 'Audit and compliance role with read access', false, true),
('VENDOR', 'Vendor', 'Vendor/Supplier management role', false, true),
('CUSTOMER_SUPPORT', 'Customer Support', 'Customer support team role', false, true)
ON CONFLICT (role_code) DO NOTHING;

-- Ensure sequence is in sync
select setval(pg_get_serial_sequence('role_master','role_id'), (select coalesce(max(role_id),0) from role_master));

-- =========================================================
-- END OF SEED MIGRATION V2
-- =========================================================

