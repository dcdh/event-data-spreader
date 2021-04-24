package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.eventsourcing.AggregateRootEventPayload;

import javax.json.JsonObject;

public interface JsonbAggregateRootEventPayloadDeSerializer {

    String aggregateRootType();

    String eventType();

    /**
     *
     * @param aggregateRootId use when using CryptoService feature to use the secret associated to this aggregate root identifier
     * @param aggregateRootEventPayload
     * @return
     */
    JsonObject encode(AggregateRootId aggregateRootId, AggregateRootEventPayload aggregateRootEventPayload);

    AggregateRootEventPayload decode(JsonObject json);

}
