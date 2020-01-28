package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadata;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import java.util.List;

public interface JacksonEventMetadataSubtypes {

    List<JacksonSubtype<EventMetadata>> jacksonSubtypes();

    default NamedType[] namedTypes() {
        return jacksonSubtypes()
                .stream()
                .map(JacksonSubtype::toNamedType)
                .toArray(NamedType[]::new);
    }

}
