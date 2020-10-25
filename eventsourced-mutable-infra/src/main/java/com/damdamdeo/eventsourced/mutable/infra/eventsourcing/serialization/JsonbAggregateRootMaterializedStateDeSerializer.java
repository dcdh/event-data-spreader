package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;

import javax.json.JsonObject;

public interface JsonbAggregateRootMaterializedStateDeSerializer {

    String aggregateRootType();

    JsonObject serialize(AggregateRoot aggregateRoot, boolean shouldEncrypt);

    <T extends AggregateRoot> T deserialize(AggregateRootId aggregateRootId, JsonObject aggregateRoot, Long version);

}
