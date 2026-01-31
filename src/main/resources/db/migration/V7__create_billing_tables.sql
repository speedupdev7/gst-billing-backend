-- =========================================================
-- Flyway Migration V7
-- Description : GST Billing + Payments + Credit/Debit Notes
-- Database    : PostgreSQL
-- =========================================================

-- =========================================================
-- 1. INVOICE HEADER
-- =========================================================
create table if not exists invoice_header (
    invoice_id        bigserial primary key,
    invoice_no        varchar(50) not null unique,
    invoice_date      date not null,

    unit_id           bigint not null,
    customer_id       bigint not null,

    place_of_supply   varchar(100),
    state_code        varchar(5),
    is_reverse_charge boolean default false,

    total_gross_amt   numeric(14,2) default 0,
    total_discount    numeric(14,2) default 0,
    taxable_amount    numeric(14,2) default 0,

    total_cgst        numeric(14,2) default 0,
    total_sgst        numeric(14,2) default 0,
    total_igst        numeric(14,2) default 0,
    round_off         numeric(10,2) default 0,

    final_amount      numeric(14,2) not null,

    transporter_name  varchar(200),
    vehicle_number    varchar(50),
    narration         text,

    is_active         boolean default true,
    is_deleted        boolean default false,
    deleted_at        timestamp,

    created_at        timestamp default current_timestamp,
    updated_at        timestamp default current_timestamp,

    constraint fk_invoice_unit
        foreign key (unit_id) references unit_master (unit_id),

    constraint fk_invoice_customer
        foreign key (customer_id) references customer_master (customer_id)
);

-- =========================================================
-- 2. INVOICE ITEMS
-- =========================================================
create table if not exists invoice_item (
    invoice_item_id bigserial primary key,
    invoice_id      bigint not null,
    item_id         bigint not null,

    hsn_code        varchar(20),
    quantity        numeric(12,3) not null,
    rate            numeric(12,2) not null,

    gross_amount    numeric(14,2),
    discount_pct    numeric(5,2),
    discount_amt    numeric(14,2),
    taxable_amount  numeric(14,2),

    gst_rate        numeric(5,2),
    cgst_amt        numeric(14,2),
    sgst_amt        numeric(14,2),
    igst_amt        numeric(14,2),

    line_total      numeric(14,2),

    is_deleted      boolean default false,
    deleted_at      timestamp,

    created_at      timestamp default current_timestamp,
    updated_at      timestamp default current_timestamp,

    constraint fk_invoice_item_invoice
        foreign key (invoice_id) references invoice_header (invoice_id),

    constraint fk_invoice_item_item
        foreign key (item_id) references item_master (item_id)
);

-- =========================================================
-- 3. INVOICE PAYMENTS
-- =========================================================
create table if not exists invoice_payment (
    payment_id   bigserial primary key,
    invoice_id   bigint not null,

    payment_mode varchar(30),
    amount       numeric(14,2),
    reference_no varchar(100),
    payment_date date,

    is_deleted   boolean default false,
    deleted_at   timestamp,

    created_at   timestamp default current_timestamp,
    updated_at   timestamp default current_timestamp,

    constraint fk_payment_invoice
        foreign key (invoice_id) references invoice_header (invoice_id)
);

-- =========================================================
-- 4. INVOICE BALANCE
-- =========================================================
create table if not exists invoice_balance (
    balance_id     bigserial primary key,
    invoice_id     bigint not null,

    invoice_amount numeric(14,2),
    paid_amount    numeric(14,2),
    balance_amount numeric(14,2),

    due_date       date,
    status         varchar(20),

    is_deleted     boolean default false,
    deleted_at     timestamp,

    created_at     timestamp default current_timestamp,
    updated_at     timestamp default current_timestamp,

    constraint fk_balance_invoice
        foreign key (invoice_id) references invoice_header (invoice_id)
);

-- =========================================================
-- 5. CREDIT / DEBIT NOTES (GST ADJUSTMENTS)
-- =========================================================
create table if not exists gst_adjustment_note (
    note_id              bigserial primary key,
    note_type            varchar(10) not null, -- CREDIT / DEBIT

    note_no              varchar(50) not null unique,
    note_date            date not null,

    original_invoice_id  bigint not null,
    unit_id              bigint not null,
    customer_id          bigint not null,

    reason_code          varchar(50),
    reason_text          text,

    taxable_amount       numeric(14,2),
    cgst_amount          numeric(14,2),
    sgst_amount          numeric(14,2),
    igst_amount          numeric(14,2),
    total_amount         numeric(14,2),

    is_deleted           boolean default false,
    deleted_at           timestamp,

    created_at           timestamp default current_timestamp,
    updated_at           timestamp default current_timestamp,

    constraint fk_note_invoice
        foreign key (original_invoice_id) references invoice_header (invoice_id),

    constraint fk_note_unit
        foreign key (unit_id) references unit_master (unit_id),

    constraint fk_note_customer
        foreign key (customer_id) references customer_master (customer_id)
);

-- =========================================================
-- 6. AUDIT TRIGGERS
-- =========================================================
create trigger trg_invoice_header_updated_at
before update on invoice_header
for each row execute function set_updated_at();

create trigger trg_invoice_item_updated_at
before update on invoice_item
for each row execute function set_updated_at();

create trigger trg_invoice_payment_updated_at
before update on invoice_payment
for each row execute function set_updated_at();

create trigger trg_invoice_balance_updated_at
before update on invoice_balance
for each row execute function set_updated_at();

create trigger trg_gst_adjustment_note_updated_at
before update on gst_adjustment_note
for each row execute function set_updated_at();

-- =========================================================
-- END OF MIGRATION V7
-- =========================================================
