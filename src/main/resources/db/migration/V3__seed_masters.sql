-- =========================================================
-- Flyway Migration V3
-- Description : Seed reference master data
-- Database    : PostgreSQL
-- =========================================================

-- =======================
-- ROLE MASTER SEED
-- =======================
insert into role_master (role_code, role_name, description, is_system_role)
values
    ('ADMIN', 'Administrator', 'System administrator role', true),
    ('HR', 'Human Resources', 'HR management role', false),
    ('FIN', 'Finance', 'Finance operations role', false),
    ('EMP', 'Employee', 'Default employee role', false)
on conflict (role_code) do nothing;

-- =======================
-- DESIGNATION MASTER SEED
-- =======================
insert into designation_master (designation_code, designation_name, description)
values
    ('SE', 'Software Engineer', 'Software Engineer'),
    ('SSE', 'Senior Software Engineer', 'Senior Software Engineer'),
    ('TL', 'Technical Lead', 'Team Lead'),
    ('MGR', 'Manager', 'Manager')
on conflict (designation_code) do nothing;

-- =======================
-- QUALIFICATION MASTER SEED
-- =======================
insert into qualification_master (qualification_code, qualification_name)
values
    ('BTECH', 'Bachelor of Technology'),
    ('MTECH', 'Master of Technology'),
    ('BSC', 'Bachelor of Science'),
    ('MSC', 'Master of Science'),
    ('MBA', 'Master of Business Administration')
on conflict (qualification_code) do nothing;

-- =======================
-- CITY MASTER SEED
-- =======================
insert into city_master (city_code, city_name, state_name, country)
values
    ('PUN', 'Pune', 'Maharashtra', 'India'),
    ('MUM', 'Mumbai', 'Maharashtra', 'India'),
    ('BLR', 'Bengaluru', 'Karnataka', 'India'),
    ('DEL', 'Delhi', 'Delhi', 'India')
on conflict (city_code) do nothing;

-- =======================
-- EXPENSES MASTER SEED
-- =======================
insert into expenses_master (expense_code, expense_name, is_reimbursable)
values
    ('TRVL', 'Travel Expense', true),
    ('FD', 'Food Expense', true),
    ('INT', 'Internet Expense', true),
    ('STAT', 'Stationery Expense', true)
on conflict (expense_code) do nothing;

-- =========================================================
-- END OF MIGRATION V3
-- =========================================================
