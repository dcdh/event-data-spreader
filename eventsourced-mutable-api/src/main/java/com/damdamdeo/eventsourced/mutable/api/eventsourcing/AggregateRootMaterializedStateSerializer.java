package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;

import java.util.Optional;

public interface AggregateRootMaterializedStateSerializer {

    String serialize(Optional<AggregateRootSecret> aggregateRootSecret, AggregateRoot aggregateRoot);

    String serialize(AggregateRoot aggregateRoot);

}
