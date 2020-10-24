package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumer;

import javax.json.JsonObject;

public interface JsonObjectAggregateRootEventConsumer extends AggregateRootEventConsumer<JsonObject> {
}
