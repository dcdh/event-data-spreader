CREATE TABLE IF NOT EXISTS EVENT (
    aggregaterootid character varying(255),
    aggregateroottype character varying(255),
    version bigint,
    creationdate timestamp without time zone,
    eventtype character varying(255),
    eventmetadata jsonb,
    eventpayload jsonb,
    CONSTRAINT event_pkey PRIMARY KEY (aggregaterootid, aggregateroottype, version),
    CONSTRAINT event_unique UNIQUE (aggregaterootid, aggregateroottype, version)
)!!
