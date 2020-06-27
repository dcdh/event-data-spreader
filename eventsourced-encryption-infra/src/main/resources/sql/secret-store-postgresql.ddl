CREATE TABLE IF NOT EXISTS public.SECRET_STORE (
    aggregateroottype varchar(255),
    aggregaterootid   varchar(255),
    secret            varchar(255),
    CONSTRAINT secretstore_pkey PRIMARY KEY(aggregateroottype,aggregaterootid)
)!!
