-- =========================================================
-- Flyway Migration V12
-- Add invoice sequence table and version column for optimistic locking
-- =========================================================

-- add version column to invoice_record for optimistic locking
alter table invoice_record
    add column if not exists version integer default 0;

-- create sequence table to track per-FY invoice numbers
create table if not exists invoice_sequence (
    id bigserial primary key,
    fy varchar(16) not null unique,
    last_number integer not null default 0,
    version integer default 0,

    is_active boolean default true,
    is_deleted boolean default false,
    deleted_at timestamp,

    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create trigger trg_invoice_sequence_updated_at
before update on invoice_sequence
for each row execute function set_updated_at();

-- =========================================================
-- END OF MIGRATION V12
-- =========================================================
