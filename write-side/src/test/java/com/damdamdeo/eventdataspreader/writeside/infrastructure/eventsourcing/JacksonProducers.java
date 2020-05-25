package com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing;

import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi.JacksonDynamicImplementation;
import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.aggregate.metadata.UserAggregateRootEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.aggregate.payload.AccountAggregateRootAccountDebitedAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.payload.GiftAggregateRootGiftBoughtAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.payload.GiftAggregateRootGiftOfferedAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootMaterializedState;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootEventMetadataImplementationDiscovery;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootEventPayloadImplementationDiscovery;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootMaterializedStateImplementationDiscovery;
import com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.aggregateevent.JacksonAccountAggregateRootAccountDebitedAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.aggregateevent.JacksonGiftAggregateRootGiftBoughtAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.aggregateevent.JacksonGiftAggregateRootGiftOfferedAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.materializedstate.JacksonAccountMaterializedState;
import com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.materializedstate.JacksonGiftMaterializedState;
import com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.metadata.JacksonUserAggregateRootEventMetadata;

import javax.enterprise.inject.Produces;
import java.util.List;

import static java.util.Arrays.asList;

public class JacksonProducers {

    @Produces
    public JacksonAggregateRootEventPayloadImplementationDiscovery jacksonAggregateRootEventPayloadImplementationDiscovery() {
        return new JacksonAggregateRootEventPayloadImplementationDiscovery() {
            @Override
            public List<JacksonDynamicImplementation<AggregateRootEventPayload, JacksonAggregateRootEventPayload>> jacksonDynamicImplementations() {
                return asList(
                        new JacksonDynamicImplementation<>(AccountAggregateRootAccountDebitedAggregateRootEventPayload.class,
                                JacksonAccountAggregateRootAccountDebitedAggregateRootEventPayload.class, "AccountAggregateRootAccountDebitedAggregateRootEventPayload"),
                        new JacksonDynamicImplementation<>(GiftAggregateRootGiftBoughtAggregateRootEventPayload.class,
                                JacksonGiftAggregateRootGiftBoughtAggregateRootEventPayload.class, "GiftAggregateRootGiftBoughtAggregateRootEventPayload"),
                        new JacksonDynamicImplementation<>(GiftAggregateRootGiftOfferedAggregateRootEventPayload.class,
                                JacksonGiftAggregateRootGiftOfferedAggregateRootEventPayload.class, "GiftAggregateRootGiftOfferedAggregateRootEventPayload")
                );
            }
        };
    }

    @Produces
    public JacksonAggregateRootEventMetadataImplementationDiscovery jacksonAggregateRootEventMetadataImplementationDiscovery() {
        return new JacksonAggregateRootEventMetadataImplementationDiscovery() {
            @Override
            public List<JacksonDynamicImplementation<AggregateRootEventMetadata, JacksonAggregateRootEventMetadata>> jacksonDynamicImplementations() {
                return asList(new JacksonDynamicImplementation<>(UserAggregateRootEventMetadata.class, JacksonUserAggregateRootEventMetadata.class, "UserAggregateRootEventMetadata"));
            }
        };
    }

    @Produces
    public JacksonAggregateRootMaterializedStateImplementationDiscovery jacksonAggregateRootMaterializedStateImplementationDiscovery() {
        return new JacksonAggregateRootMaterializedStateImplementationDiscovery() {
            @Override
            public List<JacksonDynamicImplementation<AggregateRoot, JacksonAggregateRootMaterializedState>> jacksonDynamicImplementations() {
                return asList(
                        new JacksonDynamicImplementation<>(AccountAggregateRoot.class, JacksonAccountMaterializedState.class, "AccountMaterializedState"),
                        new JacksonDynamicImplementation<>(GiftAggregateRoot.class, JacksonGiftMaterializedState.class, "GiftMaterializedState")
                );
            }
        };
    }

}







