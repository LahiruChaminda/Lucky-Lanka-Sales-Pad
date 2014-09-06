DROP TABLE IF EXISTS tbl_item;

DROP TABLE IF EXISTS tbl_category;

DROP TABLE IF EXISTS tbl_outlet;

DROP TABLE IF EXISTS tbl_order_detail;

DROP TABLE IF EXISTS tbl_order;

DROP TABLE IF EXISTS tbl_route;

DROP TABLE IF EXISTS tbl_payment;

DROP TABLE IF EXISTS tbl_current_payment;

DROP TABLE IF EXISTS tbl_invoice;

DROP TABLE IF EXISTS tbl_bank_branch;

DROP TABLE IF EXISTS tbl_bank;

DROP TABLE IF EXISTS tbl_posm_detail;

DROP TABLE IF EXISTS tbl_assort_item_issue;

DROP TABLE IF EXISTS tbl_assort_free;

DROP TABLE IF EXISTS tbl_assort_item;

CREATE TABLE tbl_category ( 
    categoryId          INTEGER PRIMARY KEY,
    categoryDescription TEXT    NOT NULL 
);

CREATE TABLE tbl_item ( 
    itemId                   INTEGER,
    categoryId               INTEGER NOT NULL
                                     REFERENCES tbl_category ( categoryId ) ON UPDATE CASCADE,
    itemCode                 TEXT,
    itemDescription          TEXT    CHECK ( itemDescription != '' ),
    wholeSalePrice           REAL,
    retailPrice              REAL,
    availableQuantity        INT,
    loadedQuantity           INT,
    sixPlusOneAvailability   INT     DEFAULT 0,
    minimumFreeIssueQuantity INT     DEFAULT 0,
    freeIssueQuantity        INT     DEFAULT 0,
    itemShortName            TEXT,
    freeIssueItemId          INT,
    UNIQUE ( itemId, categoryId, minimumFreeIssueQuantity ) 
);

CREATE TABLE tbl_route ( 
    routeId   INTEGER NOT NULL
                      PRIMARY KEY,
    routeName TEXT    NOT NULL 
);

CREATE TABLE tbl_outlet ( 
    outletId       INTEGER NOT NULL
                           PRIMARY KEY,
    routeId        INTEGER NOT NULL
                           REFERENCES tbl_route ( routeId ) ON DELETE CASCADE
                                                            ON UPDATE CASCADE,
    outletName     TEXT    NOT NULL,
    outletAddress  TEXT    NOT NULL,
    outletType     INT     NOT NULL
                           DEFAULT 0,
    outletDiscount REAL    DEFAULT 0
                           CHECK ( outletDiscount >= 0 
                               AND
                           outletDiscount <= 100 ) 
);

CREATE TABLE tbl_order ( 
    orderId      INTEGER           NOT NULL
                                   PRIMARY KEY AUTOINCREMENT,
    outletId     INTEGER           REFERENCES tbl_outlet ( outletId ) ON UPDATE CASCADE,
    routeId      INTEGER           NOT NULL,
    positionId   INTEGER           NOT NULL,
    invoiceTime  LONG,
    total        DECIMAL( 20, 2 )  DEFAULT 0.00,
    batteryLevel INTEGER           NOT NULL,
    longitude    REAL              NOT NULL,
    latitude     REAL              NOT NULL,
    syncStatus   INT               DEFAULT 0 
);

CREATE TABLE tbl_order_detail ( 
    orderId         INTEGER           NOT NULL
                                      REFERENCES tbl_order ( orderId ) ON DELETE CASCADE
                                                                       ON UPDATE CASCADE,
    itemId          INTEGER           NOT NULL,
    price           DECIMAL( 20, 2 )  DEFAULT 0.00
                                      NOT NULL,
    discount        DECIMAL( 20, 2 )  DEFAULT 0.00,
    quantity        INT,
    freeQuantity    INT               DEFAULT 0,
    returnQuantity  INT               DEFAULT 0,
    replaceQuantity INT               DEFAULT 0,
    sampleQuantity  INT               DEFAULT 0,
    UNIQUE ( orderId, itemId ) 
);

CREATE TABLE tbl_invoice ( 
    invoiceId   INTEGER           NOT NULL
                                  PRIMARY KEY,
    outletId    INTEGER           NOT NULL
                                  REFERENCES tbl_outlet ( outletId ) ON DELETE CASCADE
                                                                     ON UPDATE CASCADE,
    invoiceDate LONG              NOT NULL,
    amount      DECIMAL( 10, 2 )  DEFAULT 0 
);

CREATE TABLE tbl_bank ( 
    bankCode TEXT NOT NULL
                  PRIMARY KEY,
    bankName TEXT NOT NULL
                  CHECK ( bankName != '' ) 
);

CREATE TABLE tbl_bank_branch ( 
    branchId   INTEGER NOT NULL
                       PRIMARY KEY,
    bankCode   TEXT    NOT NULL
                       REFERENCES tbl_bank ( bankCode ) ON DELETE CASCADE
                                                        ON UPDATE CASCADE,
    branchName TEXT    NOT NULL
                       CHECK ( branchName != '' ) 
);

CREATE TABLE tbl_payment ( 
    paymentId   INTEGER           NOT NULL
                                  PRIMARY KEY AUTOINCREMENT,
    invoiceId   INT               NOT NULL
                                  REFERENCES tbl_invoice ( invoiceId ) ON DELETE CASCADE
                                                                       ON UPDATE CASCADE,
    paymentDate LONG              NOT NULL,
    amount      DECIMAL( 20, 2 )  NOT NULL
                                  CHECK ( amount > 0 ),
    chequeDate  LONG              DEFAULT 0,
    chequeNo    TEXT              DEFAULT '',
    bank        INT               DEFAULT 0,
    status      INT               DEFAULT 0 
);

CREATE TABLE tbl_current_payment ( 
    paymentId   INTEGER           NOT NULL
                                  PRIMARY KEY AUTOINCREMENT,
    orderId     INT               NOT NULL
                                  REFERENCES tbl_order ( orderId ) ON DELETE CASCADE
                                                                   ON UPDATE CASCADE,
    paymentDate LONG              NOT NULL,
    amount      DECIMAL( 20, 2 )  NOT NULL
                                  CHECK ( amount > 0 ),
    chequeDate  LONG              DEFAULT 0,
    chequeNo    TEXT              DEFAULT '',
    branchId    INT               DEFAULT 0,
    status      INT               DEFAULT 0 
);

CREATE TABLE tbl_posm_detail ( 
    posmDetailId    INTEGER NOT NULL
                            PRIMARY KEY,
    posmDescription TEXT,
    quantity        INT     DEFAULT 0 
);

CREATE TABLE tbl_posm_order_detail ( 
    posmOrderDetailId INTEGER NOT NULL
                              PRIMARY KEY AUTOINCREMENT,
    posmDetailId      INTEGER NOT NULL,
    orderId           INTEGER NOT NULL,
    quantity          INT,
    UNIQUE ( posmDetailId, orderId ) 
);

CREATE TABLE tbl_assort_item_issue ( 
    tbl_assort_free_idassort_free INT,
    tbl_item_iditem               INT,
    idassort_item_issue           INT,
    afi_qty                       INT 
);

CREATE TABLE tbl_assort_free ( 
    idassort_free    INT,
    af_date          DATE,
    af_time          TIME,
    af_status        INT,
    af_type          TEXT,
    af_qty           INT,
    af_sixone_status INT,
    free_user_type   INT 
);

CREATE TABLE tbl_assort_item ( 
    tbl_item_iditem INT,
    idassort_free   INT,
    idassort_item   INT 
);
