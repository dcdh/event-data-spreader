package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

public final class UnsupportedAggregateRootEventMetadataConsumer extends AggregateRootEventMetadataConsumer {

    public UnsupportedAggregateRootEventMetadataConsumer() {}

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return true;
    }

}
