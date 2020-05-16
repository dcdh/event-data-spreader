package com.damdamdeo.eventdataspreader.queryside.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.EventMetadata;
import com.damdamdeo.eventdataspreader.event.api.EventPayload;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonEventMetadataSubtypes;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.queryside.event.JacksonAccountAggregateAccountDebitedEventPayload;
import com.damdamdeo.eventdataspreader.queryside.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.queryside.event.JacksonGiftAggregateGiftBoughtEventPayload;
import com.damdamdeo.eventdataspreader.queryside.event.JacksonGiftAggregateGiftOfferedEventPayload;

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
                return Arrays.asList(new JacksonSubtype<>(JacksonAccountAggregateAccountDebitedEventPayload.class, "AccountAggregateAccountDebitedEventPayload"),
                        new JacksonSubtype<>(JacksonGiftAggregateGiftBoughtEventPayload.class, "GiftAggregateGiftBoughtEventPayload"),
                        new JacksonSubtype<>(JacksonGiftAggregateGiftOfferedEventPayload.class, "GiftAggregateGiftOfferedEventPayload"));
            }

        };
    }

}
