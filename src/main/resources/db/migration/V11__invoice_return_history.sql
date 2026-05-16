-- =========================================================
-- Flyway Migration V11
-- Description : Invoice return history tables
-- Database    : PostgreSQL
-- =========================================================

create table if not exists invoice_return (
    return_id        bigserial primary key,
    return_no        varchar(50) not null unique,
    return_date      date not null,
    invoice_id       bigint not null,
    return_type      varchar(20) default 'RETURN',
    reason_code      varchar(50),
    reason_text      text,
    remarks          text,

    total_gross_amt  numeric(14,2) default 0,
    total_discount   numeric(14,2) default 0,
    taxable_amount   numeric(14,2) default 0,
    total_cgst       numeric(14,2) default 0,
    total_sgst       numeric(14,2) default 0,
    total_igst       numeric(14,2) default 0,
    round_off        numeric(10,2) default 0,
    final_amount     numeric(14,2) default 0,

    is_active        boolean default true,
    is_deleted       boolean default false,
    deleted_at       timestamp,
    created_at       timestamp default current_timestamp,
    updated_at       timestamp default current_timestamp,

    constraint fk_return_invoice
        foreign key (invoice_id) references invoice_record (invoice_id)
);

create table if not exists invoice_return_item (
    return_item_id   bigserial primary key,
    return_id        bigint not null,
    invoice_item_id  bigint not null,
    item_id          bigint not null,
    batch_code       varchar(20),
    hsn_code         varchar(20),
    quantity         numeric(12,3) not null,
    rate             numeric(12,2) not null,
    gross_amount     numeric(14,2),
    discount_pct     numeric(5,2),
    discount_amt     numeric(14,2),
    taxable_amount   numeric(14,2),
    gst_rate         numeric(5,2),
    cgst_amt         numeric(14,2),
    sgst_amt         numeric(14,2),
    igst_amt         numeric(14,2),
    line_total       numeric(14,2),

    is_active        boolean default true,
    is_deleted       boolean default false,
    deleted_at       timestamp,
    created_at       timestamp default current_timestamp,
    updated_at       timestamp default current_timestamp,

    constraint fk_return_item_return
        foreign key (return_id) references invoice_return (return_id),
    constraint fk_return_item_invoice_item
        foreign key (invoice_item_id) references invoice_item (invoice_item_id),
    constraint fk_return_item_item
        foreign key (item_id) references master_item (item_id)
);

create trigger trg_invoice_return_updated_at
before update on invoice_return
for each row execute function set_updated_at();

create trigger trg_invoice_return_item_updated_at
before update on invoice_return_item
for each row execute function set_updated_at();

-- =========================================================
-- END OF MIGRATION V11
-- =========================================================
