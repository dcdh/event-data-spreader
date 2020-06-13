package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public interface JacksonMixInSubtypeDiscovery<T, S> {

    List<JacksonMixInSubtype<T, S>> jacksonMixinSubtypes();

    default void registerJacksonMixInSubtype(final ObjectMapper objectMapper) {
        jacksonMixinSubtypes()
                .forEach(jacksonMixInSubtype -> {
                    jacksonMixInSubtype.registerSubtype(objectMapper);
                    jacksonMixInSubtype.addMixIn(objectMapper);
                });
    }

}
