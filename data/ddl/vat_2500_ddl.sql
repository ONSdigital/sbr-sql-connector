-- Table: vat_2500

DROP TABLE IF EXISTS vat_2500;

CREATE TABLE vat_2500
(
    ref_period integer NOT NULL,
    vatref TEXT  NOT NULL,
    deathcode TEXT ,
    birthdate TEXT ,
    deathdate TEXT ,
    sic92 TEXT ,
    turnover TEXT ,
    turnover_date TEXT ,
    record_type TEXT ,
    status TEXT ,
    actiondate TEXT ,
    crn TEXT ,
    marker TEXT ,
    addressref TEXT ,
    inqcode TEXT ,
    name1 TEXT ,
    name2 TEXT ,
    name3 TEXT ,
    tradstyle1 TEXT ,
    tradstyle2 TEXT ,
    tradstyle3 TEXT ,
    address1 TEXT ,
    address2 TEXT ,
    address3 TEXT ,
    address4 TEXT ,
    address5 TEXT ,
    postcode TEXT ,
    mkr TEXT ,
    leu_id bigint,
    CONSTRAINT vat_2500_pkey PRIMARY KEY (ref_period, vatref),
    CONSTRAINT vat_leu_fk FOREIGN KEY (ref_period, leu_id)
        REFERENCES leu_2500 (ref_period, ubrn) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
