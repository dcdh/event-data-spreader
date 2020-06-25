package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization;

import com.fasterxml.jackson.annotation.JsonCreator;

public abstract class JacksonUnsupportedAggregateRootEventPayloadConsumer extends JacksonAggregateRootEventPayloadConsumer {

    @JsonCreator
    JacksonUnsupportedAggregateRootEventPayloadConsumer() {}

}
