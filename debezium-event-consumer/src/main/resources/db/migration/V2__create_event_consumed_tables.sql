--
-- Name: eventconsumed; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eventconsumed (
    aggregaterootid character varying(255) NOT NULL,
    aggregateroottype character varying(255) NOT NULL,
    version bigint,
    consumed boolean NOT NULL,
    kafkapartition integer NOT NULL,
    kafkatopic text NOT NULL,
    kafkaoffset bigint NOT NULL
);

--
-- Name: eventconsumerconsumed; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eventconsumerconsumed (
    consumerclassname text NOT NULL,
    aggregaterootid character varying(255) NOT NULL,
    aggregateroottype character varying(255) NOT NULL,
    version bigint,
    consumedat timestamp without time zone NOT NULL,
    gitcommitid character varying(255) NOT NULL
);

--
-- Name: eventconsumed eventconsumed_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eventconsumed
    ADD CONSTRAINT eventconsumed_pkey PRIMARY KEY (aggregaterootid, aggregateroottype, version);


--
-- Name: eventconsumerconsumed eventconsumerconsumed_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eventconsumerconsumed
    ADD CONSTRAINT eventconsumerconsumed_pkey PRIMARY KEY (consumerclassname, aggregaterootid, aggregateroottype, version);


--
-- Name: eventconsumerconsumed fk370195q9777rhhfx09b2hy6r8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eventconsumerconsumed
    ADD CONSTRAINT fk370195q9777rhhfx09b2hy6r8 FOREIGN KEY (aggregaterootid, aggregateroottype, version) REFERENCES public.eventconsumed(aggregaterootid, aggregateroottype, version);
