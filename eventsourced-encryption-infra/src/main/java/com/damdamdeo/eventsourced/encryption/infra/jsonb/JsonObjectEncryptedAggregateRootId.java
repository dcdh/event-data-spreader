package com.damdamdeo.eventsourced.encryption.infra.jsonb;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import javax.json.JsonObject;
import java.util.Objects;

public final class JsonObjectEncryptedAggregateRootId implements AggregateRootId {

    private final String aggregateRootType;

    private final String aggregateRootId;

    public JsonObjectEncryptedAggregateRootId(final JsonObject jsonNodeEncrypted) {
        this(jsonNodeEncrypted.getString(JsonbCryptoService.AGGREGATE_ROOT_TYPE),
                jsonNodeEncrypted.getString(JsonbCryptoService.AGGREGATE_ROOT_ID));
    }

    public JsonObjectEncryptedAggregateRootId(final String aggregateRootType,
                                              final String aggregateRootId) {
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
    }

    @Override
    public String aggregateRootType() {
        return aggregateRootType;
    }

    @Override
    public String aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonObjectEncryptedAggregateRootId)) return false;
        JsonObjectEncryptedAggregateRootId that = (JsonObjectEncryptedAggregateRootId) o;
        return Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(aggregateRootId, that.aggregateRootId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootType, aggregateRootId);
    }

    @Override
    public String toString() {
        return "JsonObjectEncryptedAggregateRootId{" +
                "aggregateRootType='" + aggregateRootType + '\'' +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                '}';
    }
}