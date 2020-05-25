package com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventMetadataConsumer;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.JacksonAggregateRootEventMetadataConsumer;

public interface JacksonAggregateRootEventMetadataConsumerImplementationDiscovery
        extends JacksonImplementationDiscovery<AggregateRootEventMetadataConsumer, JacksonAggregateRootEventMetadataConsumer> {

}
