package com.damdamdeo.eventdataspreader.queryside.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadata;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayload;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonEventMetadataSubtypes;
import com.damdamdeo.eventdataspreader.queryside.event.AccountAggregateAccountDebitedEventPayload;
import com.damdamdeo.eventdataspreader.queryside.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.queryside.event.GiftAggregateGiftBoughtEventPayload;
import com.damdamdeo.eventdataspreader.queryside.event.GiftAggregateGiftOfferedEventPayload;

import javax.enterprise.inject.Produces;
import java.util.Arrays;
import java.util.List;

public class JacksonProducers {

    @Produces
    public JacksonEventMetadataSubtypes jacksonEventMetadataSubtypes() {
        return new JacksonEventMetadataSubtypes() {

            @Override
            public List<JacksonSubtype<EventMetadata>> jacksonSubtypes() {
                return Arrays.asList(new JacksonSubtype<>(DefaultEventMetadata.class, "DefaultEventMetadata"));
            }

        };
    }

    @Produces
    public JacksonEventPayloadSubtypes jacksonEventPayloadSubtypes() {
        return new JacksonEventPayloadSubtypes() {

            @Override
            public List<JacksonSubtype<EventPayload>> jacksonSubtypes() {
                return Arrays.asList(new JacksonSubtype<>(AccountAggregateAccountDebitedEventPayload.class, "AccountAggregateAccountDebitedEventPayload"),
                        new JacksonSubtype<>(GiftAggregateGiftBoughtEventPayload.class, "GiftAggregateGiftBoughtEventPayload"),
                        new JacksonSubtype<>(GiftAggregateGiftOfferedEventPayload.class, "GiftAggregateGiftOfferedEventPayload"));
            }

        };
    }

}
