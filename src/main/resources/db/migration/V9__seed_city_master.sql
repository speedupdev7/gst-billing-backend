-- =========================================================
-- Flyway Migration V8
-- Description : Seed data for City Master
-- Database    : PostgreSQL
-- Note        : Inserts major Indian cities with their states
-- =========================================================

-- ======================
-- CITY MASTER SEED DATA
-- ======================
insert into master_city (city_code, city_name, state_name, country, is_active)
values
-- Maharashtra
('MUM', 'Mumbai', 'Maharashtra', 'India', true),
('PUN', 'Pune', 'Maharashtra', 'India', true),
('NAG', 'Nagpur', 'Maharashtra', 'India', true),
('NAS', 'Nashik', 'Maharashtra', 'India', true),
('AUR', 'Aurangabad', 'Maharashtra', 'India', true),
('THA', 'Thane', 'Maharashtra', 'India', true),
('KOL', 'Kolhapur', 'Maharashtra', 'India', true),

-- Delhi
('DEL', 'New Delhi', 'Delhi', 'India', true),
('DEL_OLD', 'Delhi', 'Delhi', 'India', true),

-- Karnataka
('BAN', 'Bengaluru', 'Karnataka', 'India', true),
('MYS', 'Mysuru', 'Karnataka', 'India', true),
('MAN', 'Mangalore', 'Karnataka', 'India', true),
('HUB', 'Hubballi', 'Karnataka', 'India', true),
('BEL', 'Belagavi', 'Karnataka', 'India', true),

-- Tamil Nadu
('CHE', 'Chennai', 'Tamil Nadu', 'India', true),
('MAD', 'Madurai', 'Tamil Nadu', 'India', true),
('COI', 'Coimbatore', 'Tamil Nadu', 'India', true),
('TRI', 'Tiruchirappalli', 'Tamil Nadu', 'India', true),
('SAL', 'Salem', 'Tamil Nadu', 'India', true),

-- Gujarat
('AHM', 'Ahmedabad', 'Gujarat', 'India', true),
('SUR', 'Surat', 'Gujarat', 'India', true),
('VAD', 'Vadodara', 'Gujarat', 'India', true),
('RAJ', 'Rajkot', 'Gujarat', 'India', true),
('BHAV', 'Bhavnagar', 'Gujarat', 'India', true),

-- Rajasthan
('JAI', 'Jaipur', 'Rajasthan', 'India', true),
('JOD', 'Jodhpur', 'Rajasthan', 'India', true),
('UDA', 'Udaipur', 'Rajasthan', 'India', true),
('KOT', 'Kota', 'Rajasthan', 'India', true),
('AJM', 'Ajmer', 'Rajasthan', 'India', true),

-- Uttar Pradesh
('LUC', 'Lucknow', 'Uttar Pradesh', 'India', true),
('KAN', 'Kanpur', 'Uttar Pradesh', 'India', true),
('VAR', 'Varanasi', 'Uttar Pradesh', 'India', true),
('AGR', 'Agra', 'Uttar Pradesh', 'India', true),
('ALL', 'Allahabad', 'Uttar Pradesh', 'India', true),

-- West Bengal
('KOLK', 'Kolkata', 'West Bengal', 'India', true),
('HOW', 'Howrah', 'West Bengal', 'India', true),
('DAR', 'Durgapur', 'West Bengal', 'India', true),
('SIL', 'Siliguri', 'West Bengal', 'India', true),
('ASR', 'Asansol', 'West Bengal', 'India', true),

-- Madhya Pradesh
('BHO', 'Bhopal', 'Madhya Pradesh', 'India', true),
('IND', 'Indore', 'Madhya Pradesh', 'India', true),
('JAB', 'Jabalpur', 'Madhya Pradesh', 'India', true),
('UJJ', 'Ujjain', 'Madhya Pradesh', 'India', true),
('GWAL', 'Gwalior', 'Madhya Pradesh', 'India', true),

-- Punjab
('AMD', 'Amritsar', 'Punjab', 'India', true),
('LUD', 'Ludhiana', 'Punjab', 'India', true),
('JAL', 'Jalandhar', 'Punjab', 'India', true),
('PAT', 'Patiala', 'Punjab', 'India', true),
('BAT', 'Bathinda', 'Punjab', 'India', true),

-- Haryana
('CHA', 'Chandigarh', 'Haryana', 'India', true),
('FAR', 'Faridabad', 'Haryana', 'India', true),
('GUR', 'Gurugram', 'Haryana', 'India', true),
('PAN', 'Panipat', 'Haryana', 'India', true),
('AMB', 'Ambala', 'Haryana', 'India', true),

-- Kerala
('THI', 'Thiruvananthapuram', 'Kerala', 'India', true),
('COC', 'Kochi', 'Kerala', 'India', true),
('CAL', 'Kozhikode', 'Kerala', 'India', true),
('THR', 'Thrissur', 'Kerala', 'India', true),
('KAN_KER', 'Kannur', 'Kerala', 'India', true),

-- Telangana
('HYD', 'Hyderabad', 'Telangana', 'India', true),
('WAR', 'Warangal', 'Telangana', 'India', true),
('KAR', 'Karimnagar', 'Telangana', 'India', true),
('NIZ', 'Nizamabad', 'Telangana', 'India', true),
('KHA', 'Khammam', 'Telangana', 'India', true),

-- Andhra Pradesh
('VIS', 'Visakhapatnam', 'Andhra Pradesh', 'India', true),
('VIJ', 'Vijayawada', 'Andhra Pradesh', 'India', true),
('GAN', 'Guntur', 'Andhra Pradesh', 'India', true),
('NEL', 'Nellore', 'Andhra Pradesh', 'India', true),
('KUR', 'Kurnool', 'Andhra Pradesh', 'India', true),

-- Odisha
('BUB', 'Bhubaneswar', 'Odisha', 'India', true),
('CUT', 'Cuttack', 'Odisha', 'India', true),
('ROU', 'Rourkela', 'Odisha', 'India', true),
('BER', 'Berhampur', 'Odisha', 'India', true),
('SAM', 'Sambalpur', 'Odisha', 'India', true),

-- Bihar
('PAT_BIH', 'Patna', 'Bihar', 'India', true),
('GAY', 'Gaya', 'Bihar', 'India', true),
('BHA', 'Bhagalpur', 'Bihar', 'India', true),
('MUZ', 'Muzaffarpur', 'Bihar', 'India', true),
('PUR', 'Purnia', 'Bihar', 'India', true),

-- Jharkhand
('RAN', 'Ranchi', 'Jharkhand', 'India', true),
('JAM', 'Jamshedpur', 'Jharkhand', 'India', true),
('DHA', 'Dhanbad', 'Jharkhand', 'India', true),
('BOK', 'Bokaro', 'Jharkhand', 'India', true),
('DEV', 'Deoghar', 'Jharkhand', 'India', true),

-- Assam
('GUW', 'Guwahati', 'Assam', 'India', true),
('SIL_ASS', 'Silchar', 'Assam', 'India', true),
('DIB', 'Dibrugarh', 'Assam', 'India', true),
('JOR', 'Jorhat', 'Assam', 'India', true),
('TEZ', 'Tezpur', 'Assam', 'India', true),

-- Chhattisgarh
('RAI', 'Raipur', 'Chhattisgarh', 'India', true),
('BIL', 'Bilaspur', 'Chhattisgarh', 'India', true),
('KOR', 'Korba', 'Chhattisgarh', 'India', true),
('DUR', 'Durg', 'Chhattisgarh', 'India', true),
('RAJ_CG', 'Rajpur', 'Chhattisgarh', 'India', true),

-- Uttarakhand
('DEH', 'Dehradun', 'Uttarakhand', 'India', true),
('HAR', 'Haridwar', 'Uttarakhand', 'India', true),
('ROO', 'Roorkee', 'Uttarakhand', 'India', true),
('HAL', 'Haldwani', 'Uttarakhand', 'India', true),
('KAT', 'Kashipur', 'Uttarakhand', 'India', true),

-- Himachal Pradesh
('SHI', 'Shimla', 'Himachal Pradesh', 'India', true),
('MAN_HP', 'Mandi', 'Himachal Pradesh', 'India', true),
('SOL', 'Solan', 'Himachal Pradesh', 'India', true),
('DHAR', 'Dharamshala', 'Himachal Pradesh', 'India', true),
('PAL', 'Palampur', 'Himachal Pradesh', 'India', true),

-- Jammu and Kashmir
('SRI', 'Srinagar', 'Jammu and Kashmir', 'India', true),
('JAM_JK', 'Jammu', 'Jammu and Kashmir', 'India', true),
('ANI', 'Anantnag', 'Jammu and Kashmir', 'India', true),
('BAR', 'Baramulla', 'Jammu and Kashmir', 'India', true),
('KAT_JK', 'Kathua', 'Jammu and Kashmir', 'India', true),

-- Goa
('PAN_GOA', 'Panaji', 'Goa', 'India', true),
('MAR', 'Margao', 'Goa', 'India', true),
('VAS', 'Vasco da Gama', 'Goa', 'India', true),
('PON', 'Ponda', 'Goa', 'India', true),
('MAP', 'Mapusa', 'Goa', 'India', true),

-- Puducherry
('PONDY', 'Puducherry', 'Puducherry', 'India', true),
('KARAI', 'Karaikal', 'Puducherry', 'India', true),
('MAHE', 'Mahe', 'Puducherry', 'India', true),
('YANAM', 'Yanam', 'Puducherry', 'India', true),

-- Chandigarh
('CHAN', 'Chandigarh', 'Chandigarh', 'India', true),

-- Lakshadweep
('KAV', 'Kavaratti', 'Lakshadweep', 'India', true),
('AGA', 'Agatti', 'Lakshadweep', 'India', true),
('MIN', 'Minicoy', 'Lakshadweep', 'India', true),

-- Andaman and Nicobar Islands
('POR', 'Port Blair', 'Andaman and Nicobar Islands', 'India', true),
('CAR', 'Car Nicobar', 'Andaman and Nicobar Islands', 'India', true),
('DIG', 'Diglipur', 'Andaman and Nicobar Islands', 'India', true),

-- Dadra and Nagar Haveli and Daman and Diu
('DAM', 'Daman', 'Dadra and Nagar Haveli and Daman and Diu', 'India', true),
('DIU', 'Diu', 'Dadra and Nagar Haveli and Daman and Diu', 'India', true),
('SILV', 'Silvassa', 'Dadra and Nagar Haveli and Daman and Diu', 'India', true)
ON CONFLICT (city_code) DO NOTHING;

-- Ensure sequence is in sync
select setval(pg_get_serial_sequence('master_city','city_id'), (select coalesce(max(city_id),0) from master_city));

-- =========================================================
-- END OF SEED MIGRATION V8
-- =========================================================
