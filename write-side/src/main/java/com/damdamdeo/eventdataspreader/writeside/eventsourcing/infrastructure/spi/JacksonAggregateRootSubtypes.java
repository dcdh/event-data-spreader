package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi;

import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import java.util.List;

public interface JacksonAggregateRootSubtypes {

    List<JacksonSubtype<AggregateRoot>> jacksonSubtypes();

    default NamedType[] namedTypes() {
        return jacksonSubtypes()
                .stream()
                .map(JacksonSubtype::toNamedType)
                .toArray(NamedType[]::new);
    }

}
