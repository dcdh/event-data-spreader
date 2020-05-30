package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import java.util.List;

public interface JacksonAggregateRootImplementationDiscovery<T, S> {

    List<JacksonAggregateRootDynamicImplementation<T, S>> jacksonDynamicImplementations();

    default void registerJacksonDynamicImplementations(final ObjectMapper objectMapper) {
        objectMapper.registerSubtypes(jacksonDynamicImplementations()
                .stream()
                .map(JacksonAggregateRootDynamicImplementation::toNamedType)
                .toArray(NamedType[]::new));
        jacksonDynamicImplementations()
                .forEach(jacksonAggregateRootDynamicImplementation -> jacksonAggregateRootDynamicImplementation.addMixInToObjectMapper(objectMapper));
    }

}
