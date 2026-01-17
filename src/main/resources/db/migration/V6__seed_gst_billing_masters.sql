-- =========================================================
-- Flyway Migration V6
-- Description : Seed GST Billing masters
-- =========================================================

-- =======================
-- UNIT MASTER
-- =======================
insert into unit_master (unit_name, gstin, state, state_code, email, mobile_no)
values
('Head Office', '27ABCDE1234F1Z5', 'Maharashtra', '27', 'ho@company.com', '9999999999')
on conflict do nothing;

-- =======================
-- ITEM MASTER
-- =======================
insert into item_master (item_code, item_name, hsn_code, unit, gst_rate, price)
values
('ITEM001', 'Consulting Service', '9983', 'NOS', 18.00, 5000.00),
('ITEM002', 'Software License', '8523', 'NOS', 18.00, 12000.00)
on conflict do nothing;

-- =======================
-- CUSTOMER MASTER
-- =======================
insert into customer_master (customer_name, gstin, state, state_code, email)
values
('ABC Enterprises', '29ABCDE1234F1Z9', 'Karnataka', '29', 'accounts@abc.com')
on conflict do nothing;

-- =======================
-- SUPPLIER MASTER
-- =======================
insert into supplier_master (supplier_name, gstin, state, state_code, email)
values
('XYZ Suppliers', '24ABCDE1234F1Z2', 'Gujarat', '24', 'sales@xyz.com')
on conflict do nothing;
