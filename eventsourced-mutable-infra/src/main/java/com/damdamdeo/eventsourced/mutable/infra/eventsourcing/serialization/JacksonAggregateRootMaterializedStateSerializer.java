package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface JacksonAggregateRootMaterializedStateSerializer {

    String aggregateRootType();

    JsonNode encode(AggregateRoot aggregateRoot, boolean shouldEncrypt, ObjectMapper objectMapper);

}
