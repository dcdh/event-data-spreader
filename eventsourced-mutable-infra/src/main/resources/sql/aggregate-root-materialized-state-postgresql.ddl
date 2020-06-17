CREATE TABLE IF NOT EXISTS AGGREGATE_ROOT_MATERIALIZED_STATE (
    aggregaterootid character varying(255) NOT NULL,
    aggregateroottype character varying(255) NOT NULL,
    serializedmaterializedstate jsonb,
    version bigint,
    gitcommitid character varying(255) NOT NULL,
    CONSTRAINT aggregaterootmaterializedstate_pkey PRIMARY KEY (aggregaterootid, aggregateroottype)
)!!
