package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

public final class UnsupportedAggregateRootMaterializedStateConsumer extends AggregateRootMaterializedStateConsumer {

    public UnsupportedAggregateRootMaterializedStateConsumer(final String aggregateRootId,
                                                             final String aggregateRootType,
                                                             final Long version) {
        super(aggregateRootId, aggregateRootType, version);
    }

}
