--
-- Name: account; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.account (
    owner character varying(255) NOT NULL,
    balance numeric(19,2) NOT NULL,
    version bigint NOT NULL
);


ALTER TABLE public.account OWNER TO postgresql;

--
-- Name: gift; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gift (
    name character varying(255) NOT NULL,
    offeredto character varying(255),
    version bigint NOT NULL
);


ALTER TABLE public.gift OWNER TO postgresql;

--
-- Name: account account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (owner);

--
-- Name: gift gift_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gift
    ADD CONSTRAINT gift_pkey PRIMARY KEY (name);

