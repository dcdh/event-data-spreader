CREATE TABLE IF NOT EXISTS SecretStore (
    aggregateRootType varchar(255),
    aggregateRootId   varchar(255),
    secret            varchar(255),
    CONSTRAINT aggregateRootType_aggregateRootId PRIMARY KEY(aggregateRootType,aggregateRootId)
)!!
