package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import java.util.Objects;

public class JacksonSubtype<T> {

    private final Class<? extends T> clazz;
    private final String name;

    public JacksonSubtype(final Class<? extends T> clazz, final String name) {
        this.clazz = clazz;
        this.name = name;
    }

    public NamedType toNamedType() {
        return new NamedType(clazz, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JacksonSubtype)) return false;
        JacksonSubtype<?> that = (JacksonSubtype<?>) o;
        return Objects.equals(clazz, that.clazz) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, name);
    }
}
