package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.encryption.api.CryptoService;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.time.LocalDateTime;
import java.util.Objects;

public final class DecryptedAggregateRootEventConsumable implements AggregateRootEventConsumable<JsonObject> {

    private final AggregateRootEventId eventId;

    private final String eventType;

    private final LocalDateTime creationDate;

    private final JsonObject eventPayload;

    private final JsonObject eventMetaData;

    private final JsonObject materializedState;

    public DecryptedAggregateRootEventConsumable(final AggregateRootEventId eventId,
                                                 final String eventType,
                                                 final LocalDateTime creationDate,
                                                 final JsonObject eventPayload,
                                                 final JsonObject eventMetaData,
                                                 final JsonObject materializedState) {
        this.eventId = Objects.requireNonNull(eventId);
        this.eventType = Objects.requireNonNull(eventType);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.eventPayload = Objects.requireNonNull(eventPayload);
        this.eventMetaData = Objects.requireNonNull(eventMetaData);
        this.materializedState = Objects.requireNonNull(materializedState);
    }

    private DecryptedAggregateRootEventConsumable(final Builder builder) {
        this(
                builder.eventId,
                builder.eventType,
                builder.creationDate,
                builder.eventPayload,
                builder.eventMetaData,
                builder.materializedState
        );
    }

    @Override
    public AggregateRootEventId eventId() {
        return eventId;
    }

    @Override
    public String eventType() {
        return eventType;
    }

    @Override
    public LocalDateTime creationDate() {
        return creationDate;
    }

    @Override
    public JsonObject eventPayload() {
        return eventPayload;
    }

    @Override
    public JsonObject eventMetaData() {
        return eventMetaData;
    }

    @Override
    public JsonObject materializedState() {
        return materializedState;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private AggregateRootEventId eventId;
        private String eventType;
        private LocalDateTime creationDate;
        private JsonObject eventPayload;
        private JsonObject eventMetaData;
        private JsonObject materializedState;

        private Builder() {}

        public Builder withDebeziumJsonbAggregateRootEventConsumable(final DebeziumJsonbAggregateRootEventConsumable debeziumJsonbAggregateRootEventConsumable) {
            this.eventId = debeziumJsonbAggregateRootEventConsumable.eventId();
            this.eventType = debeziumJsonbAggregateRootEventConsumable.eventType();
            this.creationDate = debeziumJsonbAggregateRootEventConsumable.creationDate();
            this.eventPayload = debeziumJsonbAggregateRootEventConsumable.eventPayload();
            this.eventMetaData = debeziumJsonbAggregateRootEventConsumable.eventMetaData();
            this.materializedState = debeziumJsonbAggregateRootEventConsumable.materializedState();
            return this;
        }

        public DecryptedAggregateRootEventConsumable build(final CryptoService<JsonValue, JsonObject> jsonCryptoService) {
            this.eventPayload = jsonCryptoService.recursiveDecrypt(this.eventPayload);
            this.eventMetaData = jsonCryptoService.recursiveDecrypt(this.eventMetaData);
            this.materializedState = jsonCryptoService.recursiveDecrypt(this.materializedState);
            return new DecryptedAggregateRootEventConsumable(this);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecryptedAggregateRootEventConsumable that = (DecryptedAggregateRootEventConsumable) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(eventPayload, that.eventPayload) &&
                Objects.equals(eventMetaData, that.eventMetaData) &&
                Objects.equals(materializedState, that.materializedState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventType, creationDate, eventPayload, eventMetaData, materializedState);
    }

    @Override
    public String toString() {
        return "DecryptedAggregateRootEventConsumable{" +
                "eventId=" + eventId +
                ", eventType='" + eventType + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}
