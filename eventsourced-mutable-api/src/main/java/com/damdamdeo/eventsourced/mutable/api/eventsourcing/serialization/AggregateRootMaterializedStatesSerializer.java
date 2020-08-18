package com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;

public interface AggregateRootMaterializedStatesSerializer {

    String serialize(AggregateRoot aggregateRoot, boolean shouldEncrypt);

}
