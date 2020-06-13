package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventMetadataConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.JacksonAggregateRootEventMetadataConsumer;

public interface JacksonAggregateRootEventMetadataConsumerMixInSubtypeDiscovery
        extends JacksonMixInSubtypeDiscovery<AggregateRootEventMetadataConsumer, JacksonAggregateRootEventMetadataConsumer> {

}
