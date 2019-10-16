--
-- Name: eventconsumed; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eventconsumed (
    eventid uuid NOT NULL,
    consumed boolean NOT NULL
);

--
-- Name: eventconsumerconsumed; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eventconsumerconsumed (
    consumerclassname character varying(255) NOT NULL,
    eventid uuid NOT NULL,
    consumedat timestamp without time zone NOT NULL,
    eventconsumerconsumedentity_eventconsumerid uuid
);


--
-- Name: eventconsumed eventconsumed_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eventconsumed
    ADD CONSTRAINT eventconsumed_pkey PRIMARY KEY (eventid);


--
-- Name: eventconsumerconsumed eventconsumerconsumed_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eventconsumerconsumed
    ADD CONSTRAINT eventconsumerconsumed_pkey PRIMARY KEY (consumerclassname, eventid);


--
-- Name: eventconsumerconsumed fk370195q9777rhhfx09b2hy6r8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eventconsumerconsumed
    ADD CONSTRAINT fk370195q9777rhhfx09b2hy6r8 FOREIGN KEY (eventconsumerconsumedentity_eventconsumerid) REFERENCES public.eventconsumed(eventid);
