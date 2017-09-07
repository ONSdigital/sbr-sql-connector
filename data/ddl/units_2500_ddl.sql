-- Table: units_2500

DROP TABLE IF EXISTS units_2500;

CREATE TABLE units_2500
(
    ref_period bigint NOT NULL,
    unit_type VARCHAR(10)  NOT NULL,
    unit_id VARCHAR(400)  NOT NULL,
    CONSTRAINT units_2500_pkey PRIMARY KEY (ref_period, unit_type, unit_id)
);
