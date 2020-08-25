package com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;

public interface AggregateRootMaterializedStatesDeSerializer {

    String serialize(AggregateRoot aggregateRoot, boolean shouldEncrypt);

    <T extends AggregateRoot> T deserialize(AggregateRootMaterializedState aggregateRootMaterializedState);

}
