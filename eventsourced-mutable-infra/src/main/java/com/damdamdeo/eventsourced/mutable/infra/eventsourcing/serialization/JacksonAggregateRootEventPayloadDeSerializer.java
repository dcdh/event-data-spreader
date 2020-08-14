package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface JacksonAggregateRootEventPayloadDeSerializer {

    String aggregateRootType();

    String eventType();

    JsonNode encode(AggregateRootEventPayload aggregateRootEventPayload, ObjectMapper objectMapper);

    AggregateRootEventPayload decode(JsonNode json);

}
