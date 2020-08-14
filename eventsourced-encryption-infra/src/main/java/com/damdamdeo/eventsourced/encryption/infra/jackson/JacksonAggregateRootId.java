package com.damdamdeo.eventsourced.encryption.infra.jackson;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

public final class JacksonAggregateRootId implements AggregateRootId {

    private final String aggregateRootType;
    private final String aggregateRootId;

    public JacksonAggregateRootId(final JsonNode jsonNodeEncrypted) {
        this(jsonNodeEncrypted.get(JsonCryptoService.AGGREGATE_ROOT_TYPE).asText(),
                jsonNodeEncrypted.get(JsonCryptoService.AGGREGATE_ROOT_ID).asText());
    }

    public JacksonAggregateRootId(final String aggregateRootType,
                                  final String aggregateRootId) {
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JacksonAggregateRootId that = (JacksonAggregateRootId) o;
        return Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(aggregateRootId, that.aggregateRootId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootType, aggregateRootId);
    }

    @Override
    public String aggregateRootType() {
        return aggregateRootType;
    }

    @Override
    public String aggregateRootId() {
        return aggregateRootId;
    }

}
