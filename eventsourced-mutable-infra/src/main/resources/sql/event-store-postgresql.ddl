CREATE TABLE IF NOT EXISTS public.EVENT (
    aggregaterootid character varying(255),
    aggregateroottype character varying(255),
    version bigint,
    creationdate timestamp without time zone,
    eventtype character varying(255),
    eventmetadata jsonb,
    eventpayload jsonb,
    materializedstate jsonb,
    gitcommitid character varying(255) NOT NULL,
    CONSTRAINT event_pkey PRIMARY KEY (aggregaterootid, aggregateroottype, version),
    CONSTRAINT event_unique UNIQUE (aggregaterootid, aggregateroottype, version)
)!!
