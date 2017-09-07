-- Table: ent_2500

DROP TABLE IF EXISTS ent_2500 CASCADE;

CREATE TABLE ent_2500
(
    ref_period integer NOT NULL,
    entref bigint NOT NULL,
    ent_tradingstyle TEXT ,
    ent_address1 TEXT ,
    ent_address2 TEXT ,
    ent_address3 TEXT ,
    ent_address4 TEXT ,
    ent_address5 TEXT ,
    ent_postcode TEXT ,
    legalstatus TEXT ,
    paye_jobs TEXT ,
    employees TEXT ,
    standard_vat_turnover TEXT ,
    num_unique_payerefs integer,
    num_unique_vatrefs integer,
    contained_rep_vat_turnover TEXT ,
    CONSTRAINT ent_2500_pkey PRIMARY KEY (ref_period, entref)
);
