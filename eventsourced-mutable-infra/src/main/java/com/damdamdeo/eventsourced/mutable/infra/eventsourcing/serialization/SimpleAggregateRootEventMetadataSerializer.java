package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.mutable.eventsourcing.serialization.AggregateRootEventMetadataSerializer;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.metadata.ConnectedUserMetadataEnhancer;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.metadata.MetadataEnhancerContextHolder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimpleAggregateRootEventMetadataSerializer implements AggregateRootEventMetadataSerializer {

    @Override
    public String serialize() {
        return String.format("{\"%s\":%b, \"%s\":\"%s\"}",
                ConnectedUserMetadataEnhancer.USER_ANONYMOUS,
                MetadataEnhancerContextHolder.get(ConnectedUserMetadataEnhancer.USER_ANONYMOUS),
                ConnectedUserMetadataEnhancer.USER_NAME,
                MetadataEnhancerContextHolder.get(ConnectedUserMetadataEnhancer.USER_NAME));
    }

}
