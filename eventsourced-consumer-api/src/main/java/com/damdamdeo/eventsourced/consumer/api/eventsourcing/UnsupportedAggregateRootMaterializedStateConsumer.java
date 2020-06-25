package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import java.util.Objects;

public final class UnsupportedAggregateRootMaterializedStateConsumer extends AggregateRootMaterializedStateConsumer {

    public UnsupportedAggregateRootMaterializedStateConsumer(final String aggregateRootId,
                                                             final String aggregateRootType,
                                                             final Long version) {
        super(aggregateRootId, aggregateRootType, version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        return true;
    }

}
