package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import java.util.List;

public interface JacksonAggregateRootEventPayloadSubtypes {

    List<JacksonSubtype<AggregateRootEventPayload>> jacksonSubtypes();

    default NamedType[] namedTypes() {
        return jacksonSubtypes()
                .stream()
                .map(JacksonSubtype::toNamedType)
                .toArray(NamedType[]::new);
    }

}
