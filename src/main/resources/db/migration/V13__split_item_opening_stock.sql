-- =========================================================
-- Flyway Migration V13
-- Split master_item into separate item_opening_stock table for batch/stock/rate details
-- =========================================================

create table if not exists item_opening_stock (
    opening_stock_id bigserial primary key,
    item_id bigint not null unique,
    batch_code varchar(100),
    opening_stock integer default 0,
    purchase_price numeric(12,2),
    sale_price numeric(12,2),
    mrp numeric(12,2),

    is_active boolean default true,
    is_deleted boolean default false,
    deleted_at timestamp,

    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp,
    constraint fk_item_opening_stock_item foreign key (item_id) references master_item(item_id)
);

drop trigger if exists trg_item_opening_stock_updated_at on item_opening_stock;

create trigger trg_item_opening_stock_updated_at
before update on item_opening_stock
for each row execute function set_updated_at();

insert into item_opening_stock (item_id, batch_code, opening_stock, purchase_price, sale_price, mrp, is_active, is_deleted, deleted_at, created_at, updated_at)
select item_id, batch_code, opening_stock, purchase_price, sale_price, mrp, is_active, is_deleted, deleted_at, created_at, updated_at
from master_item
where item_id not in (select item_id from item_opening_stock);

alter table master_item drop column if exists batch_code;
alter table master_item drop column if exists opening_stock;
alter table master_item drop column if exists purchase_price;
alter table master_item drop column if exists sale_price;
alter table master_item drop column if exists mrp;

-- =========================================================
-- END OF MIGRATION V13
-- =========================================================
