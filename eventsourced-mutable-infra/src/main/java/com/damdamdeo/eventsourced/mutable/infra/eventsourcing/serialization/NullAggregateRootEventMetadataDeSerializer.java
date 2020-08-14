package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadataDeSerializer;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NullAggregateRootEventMetadataDeSerializer implements AggregateRootEventMetadataDeSerializer {

    @Override
    public String serialize() {
        return "{}";
    }

}
