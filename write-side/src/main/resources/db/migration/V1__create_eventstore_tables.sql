--
-- PostgreSQL database dump
--

--
-- Name: aggregaterootprojection; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.aggregaterootprojection (
    aggregaterootid character varying(255) NOT NULL,
    aggregateroottype character varying(255) NOT NULL,
    aggregateroot jsonb,
    version bigint
);


ALTER TABLE public.aggregaterootprojection OWNER TO postgresql;

--
-- Name: event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event (
    eventid uuid NOT NULL,
    aggregaterootid character varying(255),
    aggregateroottype character varying(255),
    creationdate timestamp without time zone,
    eventtype character varying(255),
    metadata jsonb,
    eventPayload jsonb,
    version bigint
);


ALTER TABLE public.event OWNER TO postgresql;

--
-- Name: aggregaterootprojection aggregaterootprojection_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.aggregaterootprojection
    ADD CONSTRAINT aggregaterootprojection_pkey PRIMARY KEY (aggregaterootid, aggregateroottype);


--
-- Name: event event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_pkey PRIMARY KEY (eventid);


--
-- PostgreSQL database dump complete
--

