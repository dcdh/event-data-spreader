package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumer;
import com.fasterxml.jackson.databind.JsonNode;

public interface JsonNodeAggregateRootEventConsumer extends AggregateRootEventConsumer<JsonNode> {
}
