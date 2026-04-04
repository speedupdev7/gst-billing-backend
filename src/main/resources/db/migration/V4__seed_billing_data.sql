-- =========================================================
-- Flyway Migration V4
-- Description : Seed data for GST Billing masters + transactions
-- Database    : PostgreSQL
-- Note        : Inserts at least 10 records into each table and
--               sets serial sequences to the correct values.
-- =========================================================

-- ======================
-- 1. UNITS (unit_master)
-- ======================
insert into unit_master (unit_name, gstin, address, state, state_code, email, mobile_no, is_active)
values
('Acme Industries', '27AAACM1234A1Z1', '123 Industrial Area, Pune', 'Maharashtra', '27', 'acme@example.com', '9876543210', true),
('Beta Traders', '07AABBT1234B1Z2', '45 Market Road, Delhi', 'Delhi', '07', 'beta@example.com', '9876500001', true),
('Gamma Enterprises', '29AAAGM1234C1Z3', '9 Residency Rd, Bengaluru', 'Karnataka', '29', 'gamma@example.com', '9876500002', true),
('Delta Pvt Ltd', '33AADDT1234D1Z4', '77 Business Park, Chennai', 'Tamil Nadu', '33', 'delta@example.com', '9876500003', true),
('Epsilon Co', '19AAAEI1234E1Z5', '12 Hill St, Jaipur', 'Rajasthan', '08', 'epsilon@example.com', '9876500004', true),
('Zeta Solutions', '24AAAZT1234F1Z6', '88 Tech Park, Surat', 'Gujarat', '24', 'zeta@example.com', '9876500005', true),
('Eta Logistics', '10AAAEL1234G1Z7', '3 Cargo Ln, Kolkata', 'West Bengal', '19', 'eta@example.com', '9876500006', true),
('Theta Services', '36AAATH1234H1Z8', '56 Service Ave, Mumbai', 'Maharashtra', '27', 'theta@example.com', '9876500007', true),
('Iota Retail', '22AAAI1234I1Z9', '101 Mall Rd, Lucknow', 'Uttar Pradesh', '09', 'iota@example.com', '9876500008', true),
('Kappa Manufacturing', '17AAAKP1234J1Z0', '200 Factory St, Bhopal', 'Madhya Pradesh', '23', 'kappa@example.com', '9876500009', true)
ON CONFLICT DO NOTHING;

-- Ensure sequence is in sync
select setval(pg_get_serial_sequence('unit_master','unit_id'), (select coalesce(max(unit_id),0) from unit_master));

-- ======================
-- 2. ITEMS (item_master)
-- ======================
insert into item_master (item_code, item_name, hsn_code, unit, gst_rate, price, is_active)
values
('ITM-1001', 'Plain T-Shirt', '6109', 'PCS', 18.00, 299.00, true),
('ITM-1002', 'Formal Shirt', '6205', 'PCS', 12.00, 799.00, true),
('ITM-1003', 'Jeans', '6203', 'PCS', 18.00, 1299.00, true),
('ITM-1004', 'Laptop Model A', '8471', 'PCS', 18.00, 45999.00, true),
('ITM-1005', 'USB Cable', '8544', 'PCS', 18.00, 199.00, true),
('ITM-1006', 'Office Chair', '9401', 'PCS', 18.00, 5999.00, true),
('ITM-1007', 'Notebook 200pg', '4820', 'PCS', 12.00, 49.00, true),
('ITM-1008', 'Pen Pack', '9608', 'PCS', 12.00, 99.00, true),
('ITM-1009', 'LED Bulb 9W', '8539', 'PCS', 18.00, 249.00, true),
('ITM-1010', 'Coffee Mug', '6912', 'PCS', 18.00, 149.00, true)
ON CONFLICT DO NOTHING;

select setval(pg_get_serial_sequence('item_master','item_id'), (select coalesce(max(item_id),0) from item_master));

-- ==========================
-- 3. CUSTOMERS (customer_master)
-- ==========================
insert into customer_master (customer_name, gstin, state, state_code, email, mobile_no, customer_type, pin_code, district, billing_address, is_active)
values
('Sunrise Retailers', '27AASRS1234K1Z1', 'Maharashtra', '27', 'sunrise@example.com', '9000000001', 'B2B', '411001', 'Pune', '10 Retail Lane, Pune', true),
('Moonlight Stores', '07AAMLS1234L1Z2', 'Delhi', '07', 'moonlight@example.com', '9000000002', 'B2C', '110001', 'New Delhi', '5 Market St, Delhi', true),
('Stellar Traders', '29AASTR1234M1Z3', 'Karnataka', '29', 'stellar@example.com', '9000000003', 'B2B', '560001', 'Bengaluru', '22 Trade Park, Bengaluru', true),
('Cosmic Enterprises', '33AACOS1234N1Z4', 'Tamil Nadu', '33', 'cosmic@example.com', '9000000004', 'B2B', '600001', 'Chennai', '78 Business Rd, Chennai', true),
('Desert Mart', '08AADM1234O1Z5', 'Rajasthan', '08', 'desert@example.com', '9000000005', 'B2C', '302001', 'Jaipur', '3 Shopping Sq, Jaipur', true),
('Gujarat Wholesale', '24AAGW1234P1Z6', 'Gujarat', '24', 'gw@example.com', '9000000006', 'B2B', '395007', 'Surat', '99 Wholesale Ave, Surat', true),
('Bengal Distributors', '19AABD1234Q1Z7', 'West Bengal', '19', 'bd@example.com', '9000000007', 'B2B', '700001', 'Kolkata', '55 Distribution Rd, Kolkata', true),
('Island Imports', '36AAII1234R1Z8', 'Maharashtra', '27', 'island@example.com', '9000000008', 'B2C', '400001', 'Mumbai', '12 Harbor St, Mumbai', true),
('Lucknow Supplies', '09AALS1234S1Z9', 'Uttar Pradesh', '09', 'lucknow@example.com', '9000000009', 'B2B', '226001', 'Lucknow', '7 Supply Rd, Lucknow', true),
('Central Fabricators', '23AACF1234T1Z0', 'Madhya Pradesh', '23', 'central@example.com', '9000000010', 'B2B', '462001', 'Bhopal', '200 Fabrication St, Bhopal', true)
ON CONFLICT DO NOTHING;

select setval(pg_get_serial_sequence('customer_master','customer_id'), (select coalesce(max(customer_id),0) from customer_master));

-- ==========================
-- 4. SUPPLIERS (supplier_master)
-- ==========================
insert into supplier_master (supplier_name, gstin, address, state, state_code, email, mobile_no, is_active)
values
('Alpha Suppliers', '27AAAAS1234U1Z1', '1 Supplier Lane, Pune', 'Maharashtra', '27', 'alpha@example.com', '9111111111', true),
('Bright Wholesalers', '07AABW1234V1Z2', '2 Wholesale Rd, Delhi', 'Delhi', '07', 'bright@example.com', '9111111112', true),
('Central Parts', '29AACP1234W1Z3', '3 Parts St, Bengaluru', 'Karnataka', '29', 'centralparts@example.com', '9111111113', true),
('Delta Imports', '33AADI1234X1Z4', '4 Import Ave, Chennai', 'Tamil Nadu', '33', 'deltaimp@example.com', '9111111114', true),
('Everest Traders', '08AAET1234Y1Z5', '5 Market Ln, Jaipur', 'Rajasthan', '08', 'everest@example.com', '9111111115', true),
('Fusion Components', '24AAF1234Z1Z6', '6 Tech Park, Surat', 'Gujarat', '24', 'fusion@example.com', '9111111116', true),
('Globe Materials', '19AAGM1234A1Z7', '7 Material Rd, Kolkata', 'West Bengal', '19', 'globe@example.com', '9111111117', true),
('Horizon Supplies', '36AAHS1234B1Z8', '8 Horizon St, Mumbai', 'Maharashtra', '27', 'horizon@example.com', '9111111118', true),
('Indus Parts', '22AAIP1234C1Z9', '9 Indus Rd, Lucknow', 'Uttar Pradesh', '09', 'indus@example.com', '9111111119', true),
('Jupiter Manufacturing', '17AAJM1234D1Z0', '10 Factory Ln, Bhopal', 'Madhya Pradesh', '23', 'jupiter@example.com', '9111111120', true)
ON CONFLICT DO NOTHING;

select setval(pg_get_serial_sequence('supplier_master','supplier_id'), (select coalesce(max(supplier_id),0) from supplier_master));

-- ========================================
-- 5. INVOICES (invoice_record) - 10 records
-- ========================================
-- We'll create 10 invoices, each linked to the units/customers inserted above using subselects
insert into invoice_record (invoice_no, invoice_date, unit_id, customer_id, place_of_supply, state_code, total_gross_amt, taxable_amount, total_cgst, total_sgst, total_igst, final_amount, transporter_name)
values
('INV-2026-0001', '2026-01-05', (select unit_id from unit_master where unit_name = 'Acme Industries'), (select customer_id from customer_master where customer_name = 'Sunrise Retailers'), 'Pune', '27', 1000.00, 847.46, 76.27, 76.27, 0.00, 1000.00, 'FastTrans'),
('INV-2026-0002', '2026-01-06', (select unit_id from unit_master where unit_name = 'Beta Traders'), (select customer_id from customer_master where customer_name = 'Moonlight Stores'), 'New Delhi', '07', 1500.00, 1271.19, 114.24, 114.24, 0.00, 1500.00, 'CityLog'),
('INV-2026-0003', '2026-01-07', (select unit_id from unit_master where unit_name = 'Gamma Enterprises'), (select customer_id from customer_master where customer_name = 'Stellar Traders'), 'Bengaluru', '29', 45999.00, 38982.20, 3508.99, 3508.99, 0.00, 45999.00, 'InterMove'),
('INV-2026-0004', '2026-01-08', (select unit_id from unit_master where unit_name = 'Delta Pvt Ltd'), (select customer_id from customer_master where customer_name = 'Cosmic Enterprises'), 'Chennai', '33', 5999.00, 5084.75, 457.12, 457.13, 0.00, 5999.00, 'RoadXpress'),
('INV-2026-0005', '2026-01-09', (select unit_id from unit_master where unit_name = 'Epsilon Co'), (select customer_id from customer_master where customer_name = 'Desert Mart'), 'Jaipur', '08', 249.00, 210.17, 18.94, 18.94, 0.00, 249.00, 'ShipFast'),
('INV-2026-0006', '2026-01-10', (select unit_id from unit_master where unit_name = 'Zeta Solutions'), (select customer_id from customer_master where customer_name = 'Gujarat Wholesale'), 'Surat', '24', 99.00, 83.90, 7.05, 7.05, 0.00, 99.00, 'QuickHaul'),
('INV-2026-0007', '2026-01-11', (select unit_id from unit_master where unit_name = 'Eta Logistics'), (select customer_id from customer_master where customer_name = 'Bengal Distributors'), 'Kolkata', '19', 599.00, 507.63, 42.63, 42.64, 0.00, 599.00, 'CargoPro'),
('INV-2026-0008', '2026-01-12', (select unit_id from unit_master where unit_name = 'Theta Services'), (select customer_id from customer_master where customer_name = 'Island Imports'), 'Mumbai', '27', 149.00, 126.27, 11.37, 11.36, 0.00, 149.00, 'MetroTrans'),
('INV-2026-0009', '2026-01-13', (select unit_id from unit_master where unit_name = 'Iota Retail'), (select customer_id from customer_master where customer_name = 'Lucknow Supplies'), 'Lucknow', '09', 1299.00, 1100.85, 98.57, 98.58, 0.00, 1299.00, 'RuralShip'),
('INV-2026-0010', '2026-01-14', (select unit_id from unit_master where unit_name = 'Kappa Manufacturing'), (select customer_id from customer_master where customer_name = 'Central Fabricators'), 'Bhopal', '23', 49.00, 41.53, 3.72, 3.75, 0.00, 49.00, 'TinyMove')
ON CONFLICT DO NOTHING;

select setval(pg_get_serial_sequence('invoice_record','invoice_id'), (select coalesce(max(invoice_id),0) from invoice_record));

-- ========================================
-- 6. INVOICE ITEMS (invoice_item) - 10 items (one per invoice)
-- ========================================
insert into invoice_item (invoice_id, item_id, batch_code, hsn_code, quantity, rate, gross_amount, discount_pct, discount_amt, taxable_amount, gst_rate, cgst_amt, sgst_amt, igst_amt, line_total)
values
((select invoice_id from invoice_record where invoice_no='INV-2026-0001'), (select item_id from item_master where item_code='ITM-1001'), 'BATCH-01','6109', 2, 299.00, 598.00, 0, 0.00, 598.00, 18.00, 53.82, 53.82, 0.00, 705.64),
((select invoice_id from invoice_record where invoice_no='INV-2026-0002'), (select item_id from item_master where item_code='ITM-1002'), 'BATCH-02','6205', 1, 799.00, 799.00, 0, 0.00, 799.00, 12.00, 47.94, 47.94, 0.00, 894.88),
((select invoice_id from invoice_record where invoice_no='INV-2026-0003'), (select item_id from item_master where item_code='ITM-1004'), 'BATCH-03','8471', 1, 45999.00, 45999.00, 0, 0.00, 45999.00, 18.00, 4139.91, 4139.91, 0.00, 54278.82),
((select invoice_id from invoice_record where invoice_no='INV-2026-0004'), (select item_id from item_master where item_code='ITM-1006'), 'BATCH-04','9401', 1, 5999.00, 5999.00, 0, 0.00, 5999.00, 18.00, 539.91, 539.91, 0.00, 7078.82),
((select invoice_id from invoice_record where invoice_no='INV-2026-0005'), (select item_id from item_master where item_code='ITM-1009'), 'BATCH-05','8539', 1, 249.00, 249.00, 0, 0.00, 249.00, 18.00, 22.41, 22.41, 0.00, 293.82),
((select invoice_id from invoice_record where invoice_no='INV-2026-0006'), (select item_id from item_master where item_code='ITM-1008'), 'BATCH-06','9608', 1, 99.00, 99.00, 0, 0.00, 99.00, 12.00, 5.94, 5.94, 0.00, 110.88),
((select invoice_id from invoice_record where invoice_no='INV-2026-0007'), (select item_id from item_master where item_code='ITM-1003'), 'BATCH-07','6203', 1, 1299.00, 1299.00, 0, 0.00, 1299.00, 18.00, 116.91, 116.91, 0.00, 1532.82),
((select invoice_id from invoice_record where invoice_no='INV-2026-0008'), (select item_id from item_master where item_code='ITM-1010'), 'BATCH-08','6912', 1, 149.00, 149.00, 0, 0.00, 149.00, 18.00, 13.41, 13.41, 0.00, 175.82),
((select invoice_id from invoice_record where invoice_no='INV-2026-0009'), (select item_id from item_master where item_code='ITM-1005'), 'BATCH-09','8544', 2, 199.00, 398.00, 0, 0.00, 398.00, 18.00, 35.82, 35.82, 0.00, 469.64),
((select invoice_id from invoice_record where invoice_no='INV-2026-0010'), (select item_id from item_master where item_code='ITM-1007'), 'BATCH-10','4820', 10, 49.00, 490.00, 0, 0.00, 490.00, 12.00, 29.40, 29.40, 0.00, 548.80)
ON CONFLICT DO NOTHING;

select setval(pg_get_serial_sequence('invoice_item','invoice_item_id'), (select coalesce(max(invoice_item_id),0) from invoice_item));

-- ========================================
-- 7. INVOICE PAYMENTS (invoice_payment) - 10 payments
-- ========================================
insert into invoice_payment (invoice_id, payment_mode, amount, reference_no, payment_date)
values
((select invoice_id from invoice_record where invoice_no='INV-2026-0001'),'CASH',705.64,'CASH001','2026-01-06'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0002'),'CARD',894.88,'CARD002','2026-01-07'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0003'),'BANK',54278.82,'NEFT003','2026-01-08'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0004'),'CASH',7078.82,'CASH004','2026-01-09'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0005'),'CARD',293.82,'CARD005','2026-01-10'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0006'),'UPI',110.88,'UPI006','2026-01-11'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0007'),'BANK',1532.82,'NEFT007','2026-01-12'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0008'),'CARD',175.82,'CARD008','2026-01-13'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0009'),'CASH',469.64,'CASH009','2026-01-14'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0010'),'UPI',548.80,'UPI010','2026-01-15')
ON CONFLICT DO NOTHING;

select setval(pg_get_serial_sequence('invoice_payment','payment_id'), (select coalesce(max(payment_id),0) from invoice_payment));

-- ========================================
-- 8. INVOICE BALANCE (invoice_balance) - 10 balances
-- ========================================
insert into invoice_balance (invoice_id, invoice_amount, paid_amount, balance_amount, due_date, status)
values
((select invoice_id from invoice_record where invoice_no='INV-2026-0001'),705.64,705.64,0.00,'2026-02-05','PAID'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0002'),894.88,300.00,594.88,'2026-02-06','PARTIAL'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0003'),54278.82,54278.82,0.00,'2026-02-07','PAID'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0004'),7078.82,0.00,7078.82,'2026-02-08','DUE'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0005'),293.82,293.82,0.00,'2026-02-09','PAID'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0006'),110.88,110.88,0.00,'2026-02-10','PAID'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0007'),1532.82,1532.82,0.00,'2026-02-11','PAID'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0008'),175.82,0.00,175.82,'2026-02-12','DUE'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0009'),469.64,469.64,0.00,'2026-02-13','PAID'),
((select invoice_id from invoice_record where invoice_no='INV-2026-0010'),548.80,200.00,348.80,'2026-02-14','PARTIAL')
ON CONFLICT DO NOTHING;

select setval(pg_get_serial_sequence('invoice_balance','balance_id'), (select coalesce(max(balance_id),0) from invoice_balance));

-- =========================================================
-- 9. GST ADJUSTMENT NOTES (gst_adjustment_note) - 10 notes
-- =========================================================
insert into gst_adjustment_note (note_type, note_no, note_date, original_invoice_id, unit_id, customer_id, reason_code, reason_text, taxable_amount, cgst_amount, sgst_amount, igst_amount, total_amount)
values
('CREDIT','CRN-0001','2026-01-20',(select invoice_id from invoice_record where invoice_no='INV-2026-0001'),(select unit_id from unit_master where unit_name='Acme Industries'),(select customer_id from customer_master where customer_name='Sunrise Retailers'),'PRICE_ERR','Price correction',100.00,9.00,9.00,0.00,118.00),
('DEBIT','DBN-0002','2026-01-21',(select invoice_id from invoice_record where invoice_no='INV-2026-0002'),(select unit_id from unit_master where unit_name='Beta Traders'),(select customer_id from customer_master where customer_name='Moonlight Stores'),'ADDNL_CHG','Additional charge',50.00,3.00,3.00,0.00,56.00),
('CREDIT','CRN-0003','2026-01-22',(select invoice_id from invoice_record where invoice_no='INV-2026-0003'),(select unit_id from unit_master where unit_name='Gamma Enterprises'),(select customer_id from customer_master where customer_name='Stellar Traders'),'DISC','Discount reversed',200.00,18.00,18.00,0.00,236.00),
('CREDIT','CRN-0004','2026-01-23',(select invoice_id from invoice_record where invoice_no='INV-2026-0004'),(select unit_id from unit_master where unit_name='Delta Pvt Ltd'),(select customer_id from customer_master where customer_name='Cosmic Enterprises'),'RET','Return processed',599.00,53.91,53.92,0.00,706.83),
('DEBIT','DBN-0005','2026-01-24',(select invoice_id from invoice_record where invoice_no='INV-2026-0005'),(select unit_id from unit_master where unit_name='Epsilon Co'),(select customer_id from customer_master where customer_name='Desert Mart'),'EXTRA','Extra packing',20.00,1.80,1.80,0.00,23.60),
('CREDIT','CRN-0006','2026-01-25',(select invoice_id from invoice_record where invoice_no='INV-2026-0006'),(select unit_id from unit_master where unit_name='Zeta Solutions'),(select customer_id from customer_master where customer_name='Gujarat Wholesale'),'PRICE_ERR','Price correction',10.00,0.90,0.90,0.00,11.80),
('DEBIT','DBN-0007','2026-01-26',(select invoice_id from invoice_record where invoice_no='INV-2026-0007'),(select unit_id from unit_master where unit_name='Eta Logistics'),(select customer_id from customer_master where customer_name='Bengal Distributors'),'LATE_FEE','Late fee',30.00,2.70,2.70,0.00,35.40),
('CREDIT','CRN-0008','2026-01-27',(select invoice_id from invoice_record where invoice_no='INV-2026-0008'),(select unit_id from unit_master where unit_name='Theta Services'),(select customer_id from customer_master where customer_name='Island Imports'),'DISC','Discount given',15.00,1.35,1.35,0.00,17.70),
('DEBIT','DBN-0009','2026-01-28',(select invoice_id from invoice_record where invoice_no='INV-2026-0009'),(select unit_id from unit_master where unit_name='Iota Retail'),(select customer_id from customer_master where customer_name='Lucknow Supplies'),'HANDLING','Handling charge',25.00,2.25,2.25,0.00,29.50),
('CREDIT','CRN-0010','2026-01-29',(select invoice_id from invoice_record where invoice_no='INV-2026-0010'),(select unit_id from unit_master where unit_name='Kappa Manufacturing'),(select customer_id from customer_master where customer_name='Central Fabricators'),'ADJ','Misc adjustment',5.00,0.45,0.45,0.00,5.90)
ON CONFLICT DO NOTHING;

select setval(pg_get_serial_sequence('gst_adjustment_note','note_id'), (select coalesce(max(note_id),0) from gst_adjustment_note));

-- =========================================================
-- END OF SEED MIGRATION V4
-- =========================================================
