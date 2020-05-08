package com.damdamdeo.eventdataspreader.writeside.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.EventMetadata;
import com.damdamdeo.eventdataspreader.event.api.EventPayload;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonEventMetadataSubtypes;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.AccountAggregateAccountDebitedEventPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftAggregateGiftBoughtEventPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftAggregateGiftOfferedEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.TestAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootSubtypes;

import javax.enterprise.inject.Produces;
import java.util.List;

import static java.util.Arrays.asList;

public class JacksonProducers {

    @Produces
    public JacksonAggregateRootEventPayloadSubtypes jacksonAggregateRootEventPayloadSubtypes() {
        return new JacksonAggregateRootEventPayloadSubtypes() {

            @Override
            public List<JacksonSubtype<AggregateRootEventPayload>> jacksonSubtypes() {
                return asList(new JacksonSubtype<>(AccountAggregateAccountDebitedEventPayload.class, "AccountAggregateAccountDebitedEventPayload"),
                        new JacksonSubtype<>(GiftAggregateGiftBoughtEventPayload.class, "GiftAggregateGiftBoughtEventPayload"),
                        new JacksonSubtype<>(GiftAggregateGiftOfferedEventPayload.class, "GiftAggregateGiftOfferedEventPayload"),
                        new JacksonSubtype<>(TestAggregateRootEventPayload.class, "TestAggregateRootEventPayload"));
            }

        };
    }

    @Produces
    public JacksonEventMetadataSubtypes jacksonEventMetadataSubtypes() {
        return new JacksonEventMetadataSubtypes() {

            @Override
            public List<JacksonSubtype<EventMetadata>> jacksonSubtypes() {
                return asList(new JacksonSubtype<>(DefaultEventMetadata.class, "DefaultEventMetadata"));
            }

        };
    }

    @Produces
    public JacksonEventPayloadSubtypes jacksonEventPayloadSubtypes() {
        return new JacksonEventPayloadSubtypes() {

            @Override
            public List<JacksonSubtype<EventPayload>> jacksonSubtypes() {
                return asList(new JacksonSubtype<>(com.damdamdeo.eventdataspreader.writeside.query.event.GiftAggregateGiftBoughtEventPayload.class, "GiftAggregateGiftBoughtEventPayload"));
            }

        };
    }

    @Produces
    public JacksonAggregateRootSubtypes jacksonAggregateRootSubtypes() {
        return new JacksonAggregateRootSubtypes() {

            @Override
            public List<JacksonSubtype<AggregateRoot>> jacksonSubtypes() {
                return asList(new JacksonSubtype<>(AccountAggregate.class, "AccountAggregate"),
                        new JacksonSubtype<>(GiftAggregate.class, "GiftAggregate"));
            }

        };
    }

}
