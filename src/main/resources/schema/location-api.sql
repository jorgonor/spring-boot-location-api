CREATE SEQUENCE location.location_id_seq;

-- Table: location.location

-- DROP TABLE IF EXISTS location.location;

CREATE TABLE IF NOT EXISTS location.location
(
    id bigint NOT NULL DEFAULT nextval('location.location_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    description text COLLATE pg_catalog."default",
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    created_at timestamp with time zone NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    CONSTRAINT location_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS location.location
    OWNER to postgres;

-- Table: location.location_tag

-- DROP TABLE IF EXISTS location.location_tag;

CREATE TABLE IF NOT EXISTS location.location_tag
(
    location_id bigint NOT NULL,
    tag character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT location_tag_pkey PRIMARY KEY (location_id, tag),
    CONSTRAINT location_tag_location_fk FOREIGN KEY (location_id)
        REFERENCES location.location (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS location.location_tag
    OWNER to postgres;
-- Index: fki_location_tag_location_fk

-- DROP INDEX IF EXISTS location.fki_location_tag_location_fk;

CREATE INDEX IF NOT EXISTS fki_location_tag_location_fk
    ON location.location_tag USING btree
    (location_id ASC NULLS LAST)
    TABLESPACE pg_default;