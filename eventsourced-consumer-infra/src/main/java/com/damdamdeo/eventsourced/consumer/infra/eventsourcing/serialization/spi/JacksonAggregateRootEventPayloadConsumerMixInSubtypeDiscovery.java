package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventPayloadConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.JacksonAggregateRootEventPayloadConsumer;

public interface JacksonAggregateRootEventPayloadConsumerMixInSubtypeDiscovery
        extends JacksonMixInSubtypeDiscovery<AggregateRootEventPayloadConsumer, JacksonAggregateRootEventPayloadConsumer> {

}