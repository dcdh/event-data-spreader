package com.damdamdeo.eventdataspreader.writeside.infrastructure.consumer.aggregateevent.metadata;

import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.JacksonAggregateRootEventMetadataConsumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JacksonUserAggregateRootEventMetadataConsumer extends JacksonAggregateRootEventMetadataConsumer {

    @JsonCreator
    public JacksonUserAggregateRootEventMetadataConsumer(@JsonProperty("executedBy") final String executedBy) {
    }

}