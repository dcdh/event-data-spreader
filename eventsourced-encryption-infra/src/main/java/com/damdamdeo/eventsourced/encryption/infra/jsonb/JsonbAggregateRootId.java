package com.damdamdeo.eventsourced.encryption.infra.jsonb;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import javax.json.JsonObject;
import java.util.Objects;

public final class JsonbAggregateRootId implements AggregateRootId {

    private final String aggregateRootType;
    private final String aggregateRootId;

    public JsonbAggregateRootId(final JsonObject jsonObject) {
        this(jsonObject.getString(DefaultJsonbCryptoService.AGGREGATE_ROOT_TYPE),
                jsonObject.getString(DefaultJsonbCryptoService.AGGREGATE_ROOT_ID));
    }

    private JsonbAggregateRootId(final String aggregateRootType, final String aggregateRootId) {
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonbAggregateRootId)) return false;
        JsonbAggregateRootId that = (JsonbAggregateRootId) o;
        return Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(aggregateRootId, that.aggregateRootId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootType, aggregateRootId);
    }

    @Override
    public String aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public String aggregateRootType() {
        return aggregateRootType;
    }

}
