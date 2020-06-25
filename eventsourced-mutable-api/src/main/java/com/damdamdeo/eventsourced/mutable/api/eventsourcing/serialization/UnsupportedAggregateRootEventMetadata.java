package com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization;

public final class UnsupportedAggregateRootEventMetadata extends AggregateRootEventMetadata {

    public UnsupportedAggregateRootEventMetadata() {}

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
