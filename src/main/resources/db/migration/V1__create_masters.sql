-- =========================================================
-- Flyway Migration V1
-- Description : Create master tables with soft delete
-- Database    : PostgreSQL
-- =========================================================

-- Set search path to public
SET search_path TO public;

-- =======================
-- 1. ROLE MASTER
-- =======================
create table if not exists role_master (
    role_id         bigserial primary key,
    role_code       varchar(20) not null unique,
    role_name       varchar(100) not null,
    description     text,
    is_system_role  boolean default false,
    is_active       boolean default true,

    is_deleted      boolean default false,
    deleted_at      timestamp,

    created_at      timestamp default current_timestamp,
    updated_at      timestamp default current_timestamp
);

-- =======================
-- 2. DESIGNATION MASTER
-- =======================
create table if not exists designation_master (
    designation_id   bigserial primary key,
    designation_code varchar(20) not null unique,
    designation_name varchar(100) not null,
    description      text,
    is_active        boolean default true,

    is_deleted       boolean default false,
    deleted_at       timestamp,

    created_at       timestamp default current_timestamp,
    updated_at       timestamp default current_timestamp
);

-- =======================
-- 3. QUALIFICATION MASTER
-- =======================
create table if not exists qualification_master (
    qualification_id   bigserial primary key,
    qualification_code varchar(20) not null unique,
    qualification_name varchar(100) not null,
    description        text,
    is_active          boolean default true,

    is_deleted         boolean default false,
    deleted_at         timestamp,

    created_at         timestamp default current_timestamp,
    updated_at         timestamp default current_timestamp
);

-- =======================
-- 4. CITY MASTER
-- =======================
create table if not exists city_master (
    city_id     bigserial primary key,
    city_code   varchar(20) not null unique,
    city_name   varchar(100) not null,
    state_name  varchar(100),
    country     varchar(100),
    is_active   boolean default true,

    is_deleted  boolean default false,
    deleted_at  timestamp,

    created_at  timestamp default current_timestamp,
    updated_at  timestamp default current_timestamp
);

-- =======================
-- 5. EXPENSES MASTER
-- =======================
create table if not exists expenses_master (
    expense_id      bigserial primary key,
    expense_code    varchar(20) not null unique,
    expense_name    varchar(100) not null,
    description     text,
    is_reimbursable boolean default true,
    is_active       boolean default true,

    is_deleted      boolean default false,
    deleted_at      timestamp,

    created_at      timestamp default current_timestamp,
    updated_at      timestamp default current_timestamp
);

-- =======================
-- 6. EMPLOYEE MASTER
-- =======================
create table if not exists employee_master (
    employee_id       bigserial primary key,
    employee_code     varchar(20) not null unique,
    first_name        varchar(100) not null,
    middle_name       varchar(100),
    last_name         varchar(100) not null,
    gender            varchar(10),
    date_of_birth     date,
    joining_date      date,
    mobile_no         varchar(15),
    email_id          varchar(150) unique,
    pan_number        varchar(20),
    aadhaar_number    varchar(20),
    address           text,

    designation_id    bigint not null,
    qualification_id  bigint,
    city_id           bigint,
    role_id           bigint not null,

    is_active         boolean default true,
    is_deleted        boolean default false,
    deleted_at        timestamp,

    created_at        timestamp default current_timestamp,
    updated_at        timestamp default current_timestamp,

    constraint fk_employee_designation
        foreign key (designation_id)
        references designation_master (designation_id),

    constraint fk_employee_qualification
        foreign key (qualification_id)
        references qualification_master (qualification_id),

    constraint fk_employee_city
        foreign key (city_id)
        references city_master (city_id),

    constraint fk_employee_role
        foreign key (role_id)
        references role_master (role_id)
);

-- =========================================================
-- INDEXES FOR PERFORMANCE
-- =========================================================

-- Foreign key indexes
create index if not exists idx_employee_designation_id
    on employee_master (designation_id);

create index if not exists idx_employee_qualification_id
    on employee_master (qualification_id);

create index if not exists idx_employee_city_id
    on employee_master (city_id);

create index if not exists idx_employee_role_id
    on employee_master (role_id);

-- Partial indexes for active & not deleted records
create index if not exists idx_role_active_not_deleted
    on role_master (is_active)
    where is_deleted = false;

create index if not exists idx_designation_active_not_deleted
    on designation_master (is_active)
    where is_deleted = false;

create index if not exists idx_qualification_active_not_deleted
    on qualification_master (is_active)
    where is_deleted = false;

create index if not exists idx_city_active_not_deleted
    on city_master (is_active)
    where is_deleted = false;

create index if not exists idx_expenses_active_not_deleted
    on expenses_master (is_active)
    where is_deleted = false;

create index if not exists idx_employee_active_not_deleted
    on employee_master (is_active)
    where is_deleted = false;

-- =========================================================
-- =======================
-- AUDIT FUNCTION
-- =======================
create or replace function fn_set_updated_at()
returns trigger as
$$
begin
    new.updated_at = current_timestamp;
    return new;
end;
$$ language plpgsql;

-- =======================
-- ROLE MASTER TRIGGER
-- =======================
create trigger trg_role_master_updated_at
before update on role_master
for each row
execute function fn_set_updated_at();

-- =======================
-- DESIGNATION MASTER TRIGGER
-- =======================
create trigger trg_designation_master_updated_at
before update on designation_master
for each row
execute function fn_set_updated_at();

-- =======================
-- QUALIFICATION MASTER TRIGGER
-- =======================
create trigger trg_qualification_master_updated_at
before update on qualification_master
for each row
execute function fn_set_updated_at();

-- =======================
-- CITY MASTER TRIGGER
-- =======================
create trigger trg_city_master_updated_at
before update on city_master
for each row
execute function fn_set_updated_at();

-- =======================
-- EXPENSES MASTER TRIGGER
-- =======================
create trigger trg_expenses_master_updated_at
before update on expenses_master
for each row
execute function fn_set_updated_at();

-- =======================
-- EMPLOYEE MASTER TRIGGER
-- =======================
create trigger trg_employee_master_updated_at
before update on employee_master
for each row
execute function fn_set_updated_at();

-- =========================================================
-- END OF MIGRATION V1
-- =========================================================
