package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

// TODO in event-api I should extract infrastructure into an another module ...

import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi.JacksonAggregateRootEventMetadataConsumerImplementationDiscovery;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi.JacksonAggregateRootEventPayloadConsumerImplementationDiscovery;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi.JacksonDynamicImplementation;

import javax.enterprise.inject.Produces;
import java.util.Collections;

public class JacksonProducers {

    @Produces
    public JacksonAggregateRootEventMetadataConsumerImplementationDiscovery jacksonAggregateRootEventMetadataConsumerImplementationDiscovery() {
        return () -> Collections.singletonList(
                new JacksonDynamicImplementation<>(ExecutedByAggregateRootEventMetadataConsumer.class, JacksonExecutedByAggregateRootEventMetadataConsumer.class, "ExecutedByAggregateRootEventMetadata"));
    }

    @Produces
    public JacksonAggregateRootEventPayloadConsumerImplementationDiscovery jacksonAggregateRootEventPayloadConsumerImplementationDiscovery() {
        return () -> Collections.singletonList(
                new JacksonDynamicImplementation<>(AccountAggregateAccountDebitedAggregateRootEventPayloadConsumer.class, JacksonAccountAggregateAccountDebitedAggregateRootEventPayloadConsumer.class, "AccountAggregateAccountDebitedAggregateRootEventPayload"));
    }

}
