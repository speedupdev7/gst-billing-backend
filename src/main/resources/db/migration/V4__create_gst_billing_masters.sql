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
    hsn_code   varchar(20),
    unit       varchar(20),
    gst_rate   numeric(5,2),
    price      numeric(12,2),

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
