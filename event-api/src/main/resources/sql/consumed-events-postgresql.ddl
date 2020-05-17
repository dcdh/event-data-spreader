CREATE TABLE IF NOT EXISTS CONSUMED_EVENT (
    aggregaterootid character varying(255) NOT NULL,
    aggregateroottype character varying(255) NOT NULL,
    version bigint,
    consumed boolean NOT NULL,
    consumedat timestamp without time zone,
    kafkapartition integer NOT NULL,
    kafkatopic text NOT NULL,
    kafkaoffset bigint NOT NULL,
    CONSTRAINT eventconsumed_pkey PRIMARY KEY (aggregaterootid, aggregateroottype, version)
)!!

CREATE TABLE IF NOT EXISTS CONSUMED_EVENT_CONSUMER (
    consumerclassname text NOT NULL,
    aggregaterootid character varying(255) NOT NULL,
    aggregateroottype character varying(255) NOT NULL,
    version bigint,
    consumedat timestamp without time zone NOT NULL,
    gitcommitid character varying(255) NOT NULL,
    CONSTRAINT eventconsumerconsumed_pkey PRIMARY KEY (consumerclassname, aggregaterootid, aggregateroottype, version),
    CONSTRAINT fk370195q9777rhhfx09b2hy6r8 FOREIGN KEY (aggregaterootid, aggregateroottype, version) REFERENCES CONSUMED_EVENT(aggregaterootid, aggregateroottype, version)
)!!
