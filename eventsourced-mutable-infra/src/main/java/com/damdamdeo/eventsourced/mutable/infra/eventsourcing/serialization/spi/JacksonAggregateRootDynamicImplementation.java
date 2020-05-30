package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import java.util.Objects;

public final class JacksonAggregateRootDynamicImplementation<T, S> {

    private final Class<? extends T> target;
    private final Class<? extends S> mixinSource;
    private final String name;

    public JacksonAggregateRootDynamicImplementation(final Class<? extends T> target,
                                                     final Class<? extends S> mixinSource,
                                                     final String name) {
        this.target = Objects.requireNonNull(target);
        this.mixinSource = Objects.requireNonNull(mixinSource);
        this.name = Objects.requireNonNull(name);
    }

    public NamedType toNamedType() {
        return new NamedType(target, name);
    }

    public void addMixInToObjectMapper(final ObjectMapper objectMapper) {
        objectMapper.addMixIn(target, mixinSource);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JacksonAggregateRootDynamicImplementation<?, ?> that = (JacksonAggregateRootDynamicImplementation<?, ?>) o;
        return Objects.equals(target, that.target) &&
                Objects.equals(mixinSource, that.mixinSource) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, mixinSource, name);
    }
}
