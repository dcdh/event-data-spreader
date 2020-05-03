package com.damdamdeo.eventdataspreader.event.infrastructure.spi;

import com.damdamdeo.eventdataspreader.event.api.EventPayload;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import java.util.List;

public interface JacksonEventPayloadSubtypes {

    List<JacksonSubtype<EventPayload>> jacksonSubtypes();

    default NamedType[] namedTypes() {
        return jacksonSubtypes()
                .stream()
                .map(JacksonSubtype::toNamedType)
                .toArray(NamedType[]::new);
    }

}
