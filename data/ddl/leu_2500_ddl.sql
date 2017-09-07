-- Table: leu_2500

DROP TABLE IF EXISTS leu_2500;

CREATE TABLE leu_2500
(
    ref_period integer NOT NULL,
    ubrn bigint NOT NULL,
    businessname TEXT ,
    postcode TEXT ,
    industrycode TEXT ,
    legalstatus TEXT ,
    tradingstatus TEXT ,
    turnover TEXT ,
    employmentbands TEXT ,
    entref bigint,
    CONSTRAINT leu_2500_pkey PRIMARY KEY (ref_period, ubrn),
    CONSTRAINT leu_ent_fk FOREIGN KEY (ref_period, entref)
        REFERENCES ent_2500 (ref_period, entref) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
