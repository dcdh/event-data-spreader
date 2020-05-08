package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

// TODO in event-api I should extract infrastructure into an another module ...

import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonEventMetadataSubtypes;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonSubtype;

import javax.enterprise.inject.Produces;
import java.util.Collections;

public class JacksonProducers {

    @Produces
    public JacksonEventMetadataSubtypes jacksonEventMetadataSubtypes() {
        return () -> Collections.singletonList(
                new JacksonSubtype<>(DefaultEventMetadata.class, "DefaultEventMetadata"));
    }

    @Produces
    public JacksonEventPayloadSubtypes jacksonEventPayloadSubtypes() {
        return () -> Collections.singletonList(
                new JacksonSubtype<>(AccountAggregateAccountDebitedEventPayload.class, "AccountAggregateAccountDebitedEventPayload"));
    }

}
