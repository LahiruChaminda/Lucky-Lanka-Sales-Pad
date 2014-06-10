drop table if exists tbl_item;
drop table if exists tbl_category;
drop table if exists tbl_outlet;
drop table if exists tbl_order_detail;
drop table if exists tbl_order;

create table tbl_category(
	categoryId integer primary key,
	categoryDescription text not null
);
create table tbl_item(
	itemId integer primary key,
	categoryId integer not null references tbl_category(categoryId) on update cascade,
	itemCode text,
	itemDescription text check(itemDescription!=''),
	wholeSalePrice real,
	retailPrice real,
	availableQuantity int,
	loadedQuantity int,
	freeIssueAvailability int default 0,
);
create table tbl_outlet(
	outletId integer not null primary key,
	outletName text not null,
	outletAddress text not null,
	outletType int not null default 0,
	outletDiscount real default 0 check (outletDiscount >= 0 && outletDiscount<=100)
);
create table tbl_order(
	orderId integer not null primary key autoincrement,
	outletId integer references tbl_outlet(outletId) on update cascade,
	orderDate long,
	total real,
	batteryLevel integer not null,
	longitude real not null,
	latitude real not null
);
create table tbl_order_detail(
	orderId integer not null references tbl_order(orderId) on update cascade on delete cascade,
	itemId integer not null references tbl_item(itemId) on update cascade,
	price real not null,
	discount real,
	quantity int,
	freeQuantity int default 0
);