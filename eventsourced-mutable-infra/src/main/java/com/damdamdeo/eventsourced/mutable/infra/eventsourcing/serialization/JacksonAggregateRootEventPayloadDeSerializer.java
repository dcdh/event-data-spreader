package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface JacksonAggregateRootEventPayloadDeSerializer {

    String aggregateRootType();

    String eventType();

    /**
     *
     * @param aggregateRootId use when using CryptoService feature to use the secret associated to this aggregate root identifier
     * @param aggregateRootEventPayload
     * @param objectMapper
     * @return
     */
    JsonNode encode(AggregateRootId aggregateRootId, AggregateRootEventPayload aggregateRootEventPayload, ObjectMapper objectMapper);

    AggregateRootEventPayload decode(JsonNode json);

}
