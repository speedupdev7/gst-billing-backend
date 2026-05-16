-- =========================================================
-- Flyway Migration V2.2
-- Description : Seed data for Department Master
-- Database    : PostgreSQL
-- Note        : Inserts standard departments
-- =========================================================

-- Set search path to public
SET search_path TO public;

-- ======================
-- DEPARTMENT MASTER SEED DATA
-- ======================
insert into master_department (department_code, department_name, description, is_system_department, is_active)
values
('ADMIN', 'Administration', 'Administrative department handling overall operations', true, true),
('HR', 'Human Resources', 'Human Resources and employee management', false, true),
('FINANCE', 'Finance', 'Finance and accounting department', false, true),
('IT', 'Information Technology', 'IT and technical support', false, true),
('SALES', 'Sales', 'Sales and marketing department', false, true),
('MARKETING', 'Marketing', 'Marketing and brand management', false, true),
('OPERATIONS', 'Operations', 'Operations and logistics', false, true),
('CUSTOMER_SERVICE', 'Customer Service', 'Customer support and service', false, true),
('RND', 'Research and Development', 'R&D and innovation', false, true),
('LEGAL', 'Legal', 'Legal and compliance', false, true)
ON CONFLICT (department_code) DO NOTHING;

-- Ensure sequence is in sync
select setval(pg_get_serial_sequence('master_department','department_id'), (select coalesce(max(department_id),0) from master_department));

-- =========================================================
-- END OF SEED MIGRATION V2.2
-- =========================================================