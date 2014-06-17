DROP TABLE IF EXISTS tbl_item;
DROP TABLE IF EXISTS tbl_category;
DROP TABLE IF EXISTS tbl_outlet;
DROP TABLE IF EXISTS tbl_order_detail;
DROP TABLE IF EXISTS tbl_order;

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
  freeIssueQuantity INT DEFAULT 0
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
  total REAL,
  batteryLevel INTEGER NOT NULL,
  longitude REAL NOT NULL,
  latitude REAL NOT NULL
);
CREATE TABLE tbl_order_detail (
  orderId      INTEGER NOT NULL REFERENCES tbl_order(orderId) ON UPDATE CASCADE ON DELETE CASCADE,
  itemId       INTEGER NOT NULL REFERENCES tbl_item(itemId) ON UPDATE CASCADE,
  price        REAL    NOT NULL,
  discount     REAL,
  quantity     INT,
  freeQuantity INT DEFAULT 0
);