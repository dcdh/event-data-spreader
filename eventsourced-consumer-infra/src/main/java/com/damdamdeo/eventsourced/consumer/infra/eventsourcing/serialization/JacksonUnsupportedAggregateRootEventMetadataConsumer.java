package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization;

import com.fasterxml.jackson.annotation.JsonCreator;

public abstract class JacksonUnsupportedAggregateRootEventMetadataConsumer extends JacksonAggregateRootEventMetadataConsumer {

    @JsonCreator
    public JacksonUnsupportedAggregateRootEventMetadataConsumer() {}

}
