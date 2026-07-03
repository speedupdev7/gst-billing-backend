-- =========================================================
-- Flyway Migration V17
-- Add purchase tables and purchase sequence table
-- Database    : PostgreSQL
-- =========================================================

-- =========================================================
-- 1. PURCHASE HEADER
-- =========================================================
create table if not exists purchase_record (
    purchase_id        bigserial primary key,
    purchase_no        varchar(50) not null unique,
    version            integer default 0,
    purchase_date      date not null,
    unit_id            bigint not null,
    supplier_id        bigint not null,
    place_of_supply    varchar(100),
    state_code         varchar(5),
    total_gross_amt    numeric(14,2) default 0,
    total_discount     numeric(14,2) default 0,
    taxable_amount     numeric(14,2) default 0,
    total_cgst         numeric(14,2) default 0,
    total_sgst         numeric(14,2) default 0,
    total_igst         numeric(14,2) default 0,
    round_off          numeric(10,2) default 0,
    final_amount       numeric(14,2) not null,
    transporter_name   varchar(200),
    vehicle_number     varchar(50),
    narration          text,
    is_active          boolean default true,
    is_deleted         boolean default false,
    deleted_at         timestamp,
    created_at         timestamp default current_timestamp,
    updated_at         timestamp default current_timestamp,
    constraint fk_purchase_unit
        foreign key (unit_id) references master_unit (unit_id),
    constraint fk_purchase_supplier
        foreign key (supplier_id) references master_supplier (supplier_id)
);

-- =========================================================
-- 2. PURCHASE ITEMS
-- =========================================================
create table if not exists purchase_item (
    purchase_item_id  bigserial primary key,
    purchase_id       bigint not null,
    item_id           bigint not null,
    batch_code        varchar(20),
    hsn_code          varchar(20),
    quantity          numeric(12,3) not null,
    rate              numeric(12,2) not null,
    gross_amount      numeric(14,2),
    discount_pct      numeric(5,2),
    discount_amt      numeric(14,2),
    taxable_amount    numeric(14,2),
    gst_rate          numeric(5,2),
    cgst_amt          numeric(14,2),
    sgst_amt          numeric(14,2),
    igst_amt          numeric(14,2),
    line_total        numeric(14,2),
    is_active         boolean default true,
    is_deleted        boolean default false,
    deleted_at        timestamp,
    created_at        timestamp default current_timestamp,
    updated_at        timestamp default current_timestamp,
    constraint fk_purchase_item_purchase
        foreign key (purchase_id) references purchase_record (purchase_id),
    constraint fk_purchase_item_item
        foreign key (item_id) references master_item (item_id)
);

-- =========================================================
-- 3. PURCHASE RETURN HEADER
-- =========================================================
create table if not exists purchase_return (
    return_id          bigserial primary key,
    return_no          varchar(50) not null unique,
    return_date        date not null,
    purchase_id        bigint not null,
    return_type        varchar(50),
    reason_code        varchar(50),
    reason_text        text,
    remarks            text,
    total_gross_amt    numeric(14,2) default 0,
    total_discount     numeric(14,2) default 0,
    taxable_amount     numeric(14,2) default 0,
    total_cgst         numeric(14,2) default 0,
    total_sgst         numeric(14,2) default 0,
    total_igst         numeric(14,2) default 0,
    round_off          numeric(10,2) default 0,
    final_amount       numeric(14,2) default 0,
    is_active          boolean default true,
    is_deleted         boolean default false,
    deleted_at         timestamp,
    created_at         timestamp default current_timestamp,
    updated_at         timestamp default current_timestamp,
    constraint fk_purchase_return_purchase
        foreign key (purchase_id) references purchase_record (purchase_id)
);

-- =========================================================
-- 4. PURCHASE RETURN ITEMS
-- =========================================================
create table if not exists purchase_return_item (
    return_item_id     bigserial primary key,
    return_id          bigint not null,
    purchase_item_id   bigint not null,
    item_id            bigint not null,
    batch_code         varchar(20),
    hsn_code           varchar(20),
    quantity           numeric(12,3) not null,
    rate               numeric(12,2) not null,
    gross_amount       numeric(14,2),
    discount_pct       numeric(5,2),
    discount_amt       numeric(14,2),
    taxable_amount     numeric(14,2),
    gst_rate           numeric(5,2),
    cgst_amt           numeric(14,2),
    sgst_amt           numeric(14,2),
    igst_amt           numeric(14,2),
    line_total         numeric(14,2),
    is_active          boolean default true,
    is_deleted         boolean default false,
    deleted_at         timestamp,
    created_at         timestamp default current_timestamp,
    updated_at         timestamp default current_timestamp,
    constraint fk_purchase_return_item_return
        foreign key (return_id) references purchase_return (return_id),
    constraint fk_purchase_return_item_purchase_item
        foreign key (purchase_item_id) references purchase_item (purchase_item_id),
    constraint fk_purchase_return_item_item
        foreign key (item_id) references master_item (item_id)
);

-- =========================================================
-- 5. PURCHASE SEQUENCE TABLE
-- =========================================================
create table if not exists purchase_sequence (
    id                 bigserial primary key,
    fy                 varchar(16) not null unique,
    last_number        integer not null default 0,
    version            integer default 0,
    is_active          boolean default true,
    is_deleted         boolean default false,
    deleted_at         timestamp,
    created_at         timestamp default current_timestamp,
    updated_at         timestamp default current_timestamp
);

-- =========================================================
-- 6. AUDIT TRIGGERS
-- =========================================================
drop trigger if exists trg_purchase_record_updated_at on purchase_record;
create trigger trg_purchase_record_updated_at
before update on purchase_record
for each row execute function set_updated_at();

drop trigger if exists trg_purchase_item_updated_at on purchase_item;
create trigger trg_purchase_item_updated_at
before update on purchase_item
for each row execute function set_updated_at();

drop trigger if exists trg_purchase_return_updated_at on purchase_return;
create trigger trg_purchase_return_updated_at
before update on purchase_return
for each row execute function set_updated_at();

drop trigger if exists trg_purchase_return_item_updated_at on purchase_return_item;
create trigger trg_purchase_return_item_updated_at
before update on purchase_return_item
for each row execute function set_updated_at();

drop trigger if exists trg_purchase_sequence_updated_at on purchase_sequence;
create trigger trg_purchase_sequence_updated_at
before update on purchase_sequence
for each row execute function set_updated_at();

-- =========================================================
-- END OF MIGRATION V17
-- =========================================================
