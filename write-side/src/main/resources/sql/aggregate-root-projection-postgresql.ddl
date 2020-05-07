CREATE TABLE IF NOT EXISTS AGGREGATE_ROOT_PROJECTION (
    aggregaterootid character varying(255) NOT NULL,
    aggregateroottype character varying(255) NOT NULL,
    serializedaggregateroot jsonb,
    version bigint,
    CONSTRAINT aggregaterootprojection_pkey PRIMARY KEY (aggregaterootid, aggregateroottype)
)!!
