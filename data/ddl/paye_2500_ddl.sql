-- Table: paye_2500

DROP TABLE IF EXISTS paye_2500;

CREATE TABLE paye_2500
(
    ref_period integer NOT NULL,
    payeref TEXT  NOT NULL,
    deathcode TEXT ,
    birthdate TEXT ,
    deathdate TEXT ,
    mfullemp TEXT ,
    msubemp TEXT ,
    ffullemp TEXT ,
    fsubemp TEXT ,
    unclemp TEXT ,
    unclsubemp TEXT ,
    dec_jobs TEXT ,
    mar_jobs TEXT ,
    june_jobs TEXT ,
    sept_jobs TEXT ,
    jobs_lastupd TEXT ,
    status TEXT ,
    prevpaye TEXT ,
    employer_cat TEXT ,
    stc TEXT ,
    crn TEXT ,
    actiondate TEXT ,
    addressref TEXT ,
    marker TEXT ,
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
    CONSTRAINT paye_2500_pkey PRIMARY KEY (ref_period, payeref),
    CONSTRAINT paye_leu_fk FOREIGN KEY (ref_period, leu_id)
        REFERENCES leu_2500 (ref_period, ubrn) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

