DROP TABLE IF EXISTS tbl_item;
DROP TABLE IF EXISTS tbl_category;
DROP TABLE IF EXISTS tbl_outlet;
DROP TABLE IF EXISTS tbl_order_detail;
DROP TABLE IF EXISTS tbl_order;
DROP TABLE IF EXISTS tbl_route;
DROP TABLE IF EXISTS tbl_payment;
DROP TABLE IF EXISTS tbl_invoice;
DROP TABLE IF EXISTS tbl_bank_branch;
DROP TABLE IF EXISTS tbl_bank;

CREATE TABLE tbl_category (
    categoryId          INTEGER PRIMARY KEY,
    categoryDescription TEXT NOT NULL
);
CREATE TABLE tbl_item (
    itemId                INTEGER PRIMARY KEY,
    categoryId            INTEGER NOT NULL REFERENCES tbl_category(categoryId) ON UPDATE CASCADE,
    itemCode              TEXT,
    itemDescription       TEXT CHECK (itemDescription != ''),
    wholeSalePrice        REAL,
    retailPrice           REAL,
    availableQuantity     INT,
    loadedQuantity        INT,
    sixPlusOneAvailability INT DEFAULT 0,
    minimumFreeIssueQuantity INT DEFAULT 0,
    freeIssueQuantity INT DEFAULT 0,
    itemShortName         TEXT
);
CREATE TABLE tbl_route (
    routeId       INTEGER NOT NULL PRIMARY KEY,
    routeName     TEXT    NOT NULL
);
CREATE TABLE tbl_outlet (
    outletId       INTEGER NOT NULL PRIMARY KEY,
    routeId INTEGER NOT NULL REFERENCES tbl_route(routeId) ON UPDATE CASCADE ON DELETE CASCADE,
    outletName     TEXT    NOT NULL,
    outletAddress  TEXT    NOT NULL,
    outletType     INT     NOT NULL DEFAULT 0,
    outletDiscount REAL DEFAULT 0 CHECK (outletDiscount >= 0 AND outletDiscount <= 100)
);
CREATE TABLE tbl_order (
    orderId  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    outletId INTEGER REFERENCES tbl_outlet( outletId) ON UPDATE cascade,
    routeId INTEGER NOT NULL,
    positionId INTEGER NOT NULL,
    invoiceTime long,
    total decimal(20,2) default 0.00,
    batteryLevel INTEGER NOT NULL,
    longitude REAL NOT NULL,
    latitude REAL NOT NULL,
    syncStatus INT default 0
);
CREATE TABLE tbl_order_detail (
    orderId      INTEGER NOT NULL REFERENCES tbl_order(orderId) ON UPDATE CASCADE ON DELETE CASCADE,
    itemId       INTEGER NOT NULL REFERENCES tbl_item(itemId) ON UPDATE CASCADE,
    price        decimal(20,2) default 0.00 NOT NULL,
    discount     decimal(20,2) default 0.00,
    quantity     INT,
    freeQuantity INT DEFAULT 0,
    returnQuantity INT DEFAULT 0,
    replaceQuantity INT DEFAULT 0,
    sampleQuantity INT DEFAULT 0
);
create table tbl_invoice(
    invoiceId INTEGER NOT NULL primary key,
    outletId INTEGER NOT NULL REFERENCES tbl_outlet(outletId) ON UPDATE CASCADE ON DELETE CASCADE,
    invoiceDate long not null,
    amount decimal(10,2) default 0
);
create table tbl_bank(
    bankCode TEXT NOT NULL PRIMARY KEY,
    bankName TEXT NOT NULL CHECK (bankName!='')
);
create table tbl_bank_branch(
    branchId INTEGER NOT NULL primary key,
    bankCode TEXT NOT null references tbl_bank(bankCode) On UPDATE CASCADE On DELETE CASCADE,
    branchName Text NOT NULL CHECK (branchName !='')
);
create table tbl_payment(
   paymentId INTEGER NOT NULL primary key AUTOINCREMENT,
   invoiceId int not null references tbl_invoice(invoiceId) ON UPDATE CASCADE ON DELETE CASCADE,
   paymentDate long not null,
   amount decimal(20,2) not null check(amount > 0),
   chequeDate long default 0,
   chequeNo Text default '',
   bank int default 0,
   status int default 0
);
create table tbl_current_payment(
   paymentId INTEGER NOT NULL primary key AUTOINCREMENT,
   orderId int not null references tbl_order(orderId) ON UPDATE CASCADE ON DELETE CASCADE,
   invoiceId int not null references tbl_invoice(invoiceId) ON UPDATE CASCADE ON DELETE CASCADE,
   paymentDate long not null,
   amount decimal(20,2) not null check(amount > 0),
   chequeDate long default 0,
   chequeNo Text default '',
   branchId int default 0,
   status int default 0
);