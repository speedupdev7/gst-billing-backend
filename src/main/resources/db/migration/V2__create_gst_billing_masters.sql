-- =========================================================
-- Flyway Migration V4
-- Description : Create GST Billing Master tables
-- Database    : PostgreSQL
-- =========================================================

-- =======================
-- 1. UNIT (COMPANY) MASTER
-- =======================
create table if not exists unit_master (
    unit_id      bigserial primary key,
    unit_name    varchar(150) not null,
    gstin        varchar(15) unique,
    address      text,
    state        varchar(100),
    state_code   varchar(5),
    email        varchar(150),
    mobile_no    varchar(15),

    is_active    boolean default true,
    is_deleted   boolean default false,
    deleted_at   timestamp,

    created_at   timestamp default current_timestamp,
    updated_at   timestamp default current_timestamp
);

-- =======================
-- 2. ITEM MASTER
-- =======================
create table if not exists item_master (
    item_id    bigserial primary key,
    item_code  varchar(50) not null unique,
    item_name  varchar(200) not null,
    item_name_details varchar(500),
    hsn_code   varchar(20),
    unit       varchar(20),
    gst_rate   numeric(5,2),
    purchase_price numeric(12,2),
    sale_price numeric(12,2),
    mrp        numeric(12,2),
    opening_stock integer default 0,

    is_active  boolean default true,
    is_deleted boolean default false,
    deleted_at timestamp,

    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

-- =======================
-- 3. CUSTOMER MASTER
-- =======================
create table if not exists customer_master (
    customer_id   bigserial primary key,
    customer_name varchar(200) not null,
    gstin         varchar(15),
    state         varchar(100),
    state_code    varchar(5),
    email         varchar(150),
    mobile_no     varchar(15),
    customer_type  varchar(10) not null,
    pin_code   varchar(10),
    district    varchar(100),
    billing_address text,

    is_active     boolean default true,
    is_deleted    boolean default false,
    deleted_at    timestamp,

    created_at    timestamp default current_timestamp,
    updated_at    timestamp default current_timestamp
);

-- =======================
-- 4. SUPPLIER MASTER
-- =======================
create table if not exists supplier_master (
    supplier_id   bigserial primary key,
    supplier_name varchar(200) not null,
    gstin         varchar(15),
    address       text,
    state         varchar(100),
    state_code    varchar(5),
    email         varchar(150),
    mobile_no     varchar(15),

    is_active     boolean default true,
    is_deleted    boolean default false,
    deleted_at    timestamp,

    created_at    timestamp default current_timestamp,
    updated_at    timestamp default current_timestamp
);

-- =========================================================
--  Audit triggers for GST Billing masters
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

-- =========================================================
-- END OF MIGRATION V2
-- =========================================================