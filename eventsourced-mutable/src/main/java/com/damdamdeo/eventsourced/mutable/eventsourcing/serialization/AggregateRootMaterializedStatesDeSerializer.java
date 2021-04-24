package com.damdamdeo.eventsourced.mutable.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.eventsourcing.AggregateRoot;

public interface AggregateRootMaterializedStatesDeSerializer<T extends AggregateRoot> {

    String serialize(T aggregateRoot, boolean shouldEncrypt);

    T deserialize(AggregateRootMaterializedState aggregateRootMaterializedState);

}
