package com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import java.util.List;

public interface JacksonImplementationDiscovery<T, S> {

    List<JacksonDynamicImplementation<T, S>> jacksonDynamicImplementations();

    default void registerJacksonDynamicImplementations(final ObjectMapper objectMapper) {
        objectMapper.registerSubtypes(jacksonDynamicImplementations()
                .stream()
                .map(JacksonDynamicImplementation::toNamedType)
                .toArray(NamedType[]::new));
        jacksonDynamicImplementations()
                .forEach(jacksonDynamicImplementation -> jacksonDynamicImplementation.addMixInToObjectMapper(objectMapper));
    }

}
