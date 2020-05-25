package com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.JacksonAggregateRootEventPayloadConsumer;

public interface JacksonAggregateRootEventPayloadConsumerImplementationDiscovery
        extends JacksonImplementationDiscovery<AggregateRootEventPayloadConsumer, JacksonAggregateRootEventPayloadConsumer> {

}
