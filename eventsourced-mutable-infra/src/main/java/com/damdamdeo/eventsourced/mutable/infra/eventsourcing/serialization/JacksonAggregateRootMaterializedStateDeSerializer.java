package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface JacksonAggregateRootMaterializedStateDeSerializer {

    String aggregateRootType();

    JsonNode serialize(AggregateRoot aggregateRoot, boolean shouldEncrypt, ObjectMapper objectMapper);

    <T extends AggregateRoot> T deserialize(AggregateRootId aggregateRootId, JsonNode aggregateRoot, Long version);

}
