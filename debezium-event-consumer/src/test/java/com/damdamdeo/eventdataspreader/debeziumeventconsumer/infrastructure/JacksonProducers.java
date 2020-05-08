package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

// TODO in event-api I should extract infrastructure into an another module ...

import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonEventMetadataSubtypes;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonEventPayloadSubtypes;

import javax.enterprise.inject.Produces;
import java.util.Collections;

public class JacksonProducers {

    @Produces
    public JacksonEventMetadataSubtypes jacksonEventMetadataSubtypes() {
        return () -> Collections.emptyList();
    }

    @Produces
    public JacksonEventPayloadSubtypes jacksonEventPayloadSubtypes() {
        return () -> Collections.emptyList();
    }

}
