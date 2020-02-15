--
-- PostgreSQL database dump
--

--
-- Name: aggregateroot; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.aggregateroot (
    aggregaterootid character varying(255) NOT NULL,
    aggregateroottype character varying(255) NOT NULL,
    aggregateroot jsonb,
    version bigint
);

--
-- Name: event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event (
    aggregaterootid character varying(255),
    aggregateroottype character varying(255),
    version bigint,
    creationdate timestamp without time zone,
    encryptedeventtype character varying(255),
    secret character varying(255),
    eventtype character varying(255),
    eventmetadata jsonb,
    eventpayload jsonb
);

--
-- Name: aggregateroot aggregateroot_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.aggregateroot
    ADD CONSTRAINT aggregateroot_pkey PRIMARY KEY (aggregaterootid, aggregateroottype);


--
-- Name: event event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_pkey PRIMARY KEY (aggregaterootid, aggregateroottype, version);
ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_unique UNIQUE (aggregaterootid, aggregateroottype, version);

--
-- PostgreSQL database dump complete
--

