package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface JacksonAggregateRootMaterializedStateSerializer {

    String aggregateRootType();

    JsonNode encode(AggregateRoot aggregateRoot, Secret secret, boolean shouldEncrypt, ObjectMapper objectMapper);

}
