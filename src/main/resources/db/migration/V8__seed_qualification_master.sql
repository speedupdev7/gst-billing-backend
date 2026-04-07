-- =========================================================
-- Flyway Migration V7
-- Description : Seed data for Qualification Master
-- Database    : PostgreSQL
-- Note        : Inserts standard qualification entries
-- =========================================================

-- ======================
-- QUALIFICATION MASTER SEED DATA
-- ======================
insert into qualification_master (qualification_code, qualification_name, description, is_active)
values
('SSC', 'Secondary School Certificate', '10th Standard education qualification', true),
('HSC', 'Higher Secondary Certificate', '12th Standard education qualification', true),
('DIPLOMA', 'Diploma', 'Technical diploma qualification', true),
('BCA', 'Bachelor of Computer Applications', 'Undergraduate degree in computer applications', true),
('BSC', 'Bachelor of Science', 'Undergraduate science degree', true),
('BSC_CS', 'Bachelor of Science in Computer Science', 'Undergraduate degree in computer science', true),
('BSC_IT', 'Bachelor of Science in Information Technology', 'Undergraduate degree in information technology', true),
('BTECH', 'Bachelor of Technology', 'Engineering undergraduate degree', true),
('BE', 'Bachelor of Engineering', 'Engineering undergraduate degree', true),
('BBA', 'Bachelor of Business Administration', 'Undergraduate business degree', true),
('BBA_CA', 'Bachelor of Business Administration in Computer Applications', 'Undergraduate business degree with computer applications', true),
('BSC_MATHS', 'Bachelor of Science in Mathematics', 'Undergraduate mathematics degree', true),
('BSC_PHYSICS', 'Bachelor of Science in Physics', 'Undergraduate physics degree', true),
('BSC_CHEMISTRY', 'Bachelor of Science in Chemistry', 'Undergraduate chemistry degree', true),
('MCA', 'Master of Computer Applications', 'Postgraduate degree in computer applications', true),
('MSC', 'Master of Science', 'Postgraduate science degree', true),
('MSC_CS', 'Master of Science in Computer Science', 'Postgraduate degree in computer science', true),
('MSC_IT', 'Master of Science in Information Technology', 'Postgraduate degree in information technology', true),
('MTECH', 'Master of Technology', 'Engineering postgraduate degree', true),
('ME', 'Master of Engineering', 'Engineering postgraduate degree', true),
('MBA', 'Master of Business Administration', 'Postgraduate business degree', true),
('PHD', 'Doctor of Philosophy', 'Doctoral degree', true),
('PHD_CS', 'Doctor of Philosophy in Computer Science', 'Doctoral degree in computer science', true),
('PHD_IT', 'Doctor of Philosophy in Information Technology', 'Doctoral degree in information technology', true),
('CA', 'Chartered Accountant', 'Professional accounting qualification', true),
('CS', 'Company Secretary', 'Corporate governance qualification', true),
('ICWA', 'Institute of Cost and Works Accountants', 'Cost accounting qualification', true)
ON CONFLICT (qualification_code) DO NOTHING;

-- Ensure sequence is in sync
select setval(pg_get_serial_sequence('qualification_master','qualification_id'), (select coalesce(max(qualification_id),0) from qualification_master));

-- =========================================================
-- END OF SEED MIGRATION V7
-- =========================================================
