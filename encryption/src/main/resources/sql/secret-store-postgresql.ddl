CREATE TABLE IF NOT EXISTS SECRET_STORE (
    aggregateRootType varchar(255),
    aggregateRootId   varchar(255),
    secret            varchar(255),
    CONSTRAINT secretstore_pkey PRIMARY KEY(aggregateRootType,aggregateRootId)
)!!
