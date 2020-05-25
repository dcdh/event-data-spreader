package com.damdamdeo.eventdataspreader.writeside.infrastructure.consumer.aggregateevent;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventMetadataConsumer;
import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.JacksonAggregateRootEventMetadataConsumer;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.JacksonAggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi.JacksonAggregateRootEventMetadataConsumerImplementationDiscovery;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi.JacksonAggregateRootEventPayloadConsumerImplementationDiscovery;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi.JacksonDynamicImplementation;
import com.damdamdeo.eventdataspreader.writeside.consumer.metadata.UserAggregateRootEventMetadataConsumer;
import com.damdamdeo.eventdataspreader.writeside.consumer.payload.GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.writeside.infrastructure.consumer.aggregateevent.metadata.JacksonUserAggregateRootEventMetadataConsumer;
import com.damdamdeo.eventdataspreader.writeside.infrastructure.consumer.aggregateevent.payload.JacksonGiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer;

import javax.enterprise.inject.Produces;
import java.util.Arrays;
import java.util.List;

public class JacksonProducers {

    @Produces
    public JacksonAggregateRootEventMetadataConsumerImplementationDiscovery jacksonAggregateRootEventMetadataConsumerImplementationDiscovery() {
        return new JacksonAggregateRootEventMetadataConsumerImplementationDiscovery() {

            @Override
            public List<JacksonDynamicImplementation<AggregateRootEventMetadataConsumer, JacksonAggregateRootEventMetadataConsumer>> jacksonDynamicImplementations() {
                return Arrays.asList(
                        new JacksonDynamicImplementation<>(UserAggregateRootEventMetadataConsumer.class,
                                JacksonUserAggregateRootEventMetadataConsumer.class,
                                "UserAggregateRootEventMetadata"));
            }

        };
    }

    @Produces
    public JacksonAggregateRootEventPayloadConsumerImplementationDiscovery jacksonAggregateRootEventPayloadConsumerImplementationDiscovery() {
        return new JacksonAggregateRootEventPayloadConsumerImplementationDiscovery() {

            @Override
            public List<JacksonDynamicImplementation<AggregateRootEventPayloadConsumer, JacksonAggregateRootEventPayloadConsumer>> jacksonDynamicImplementations() {
                return Arrays.asList(
                        new JacksonDynamicImplementation<>(GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer.class,
                                JacksonGiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer.class, "GiftAggregateRootGiftBoughtAggregateRootEventPayload"));
            }

        };
    }

}
