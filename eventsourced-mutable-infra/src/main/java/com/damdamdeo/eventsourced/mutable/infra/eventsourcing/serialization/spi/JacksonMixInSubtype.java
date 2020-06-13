package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import java.util.Objects;

public final class JacksonMixInSubtype<T, S> {

    private final Class<? extends T> target;
    private final Class<? extends S> mixInSource;
    private final String subtypeName;

    public JacksonMixInSubtype(final Class<? extends T> target,
                               final Class<? extends S> mixInSource,
                               final String subtypeName) {
        this.target = Objects.requireNonNull(target);
        this.mixInSource = Objects.requireNonNull(mixInSource);
        this.subtypeName = Objects.requireNonNull(subtypeName);
    }

    public void addMixIn(final ObjectMapper objectMapper) {
        objectMapper.addMixIn(target, mixInSource);
    }

    public void registerSubtype(final ObjectMapper objectMapper) {
        objectMapper.registerSubtypes(new NamedType(target, subtypeName));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JacksonMixInSubtype<?, ?> that = (JacksonMixInSubtype<?, ?>) o;
        return Objects.equals(target, that.target) &&
                Objects.equals(mixInSource, that.mixInSource) &&
                Objects.equals(subtypeName, that.subtypeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, mixInSource, subtypeName);
    }
}
