package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import java.util.Objects;

// must be a class However it will be impossible to deserialize using Jackson
public abstract class AggregateRootMaterializedStateConsumer {

    private final String aggregateRootId;
    private final String aggregateRootType;
    private final Long version;

    public AggregateRootMaterializedStateConsumer(final String aggregateRootId,
                                                  final String aggregateRootType,
                                                  final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.version = Objects.requireNonNull(version);
    }

}
