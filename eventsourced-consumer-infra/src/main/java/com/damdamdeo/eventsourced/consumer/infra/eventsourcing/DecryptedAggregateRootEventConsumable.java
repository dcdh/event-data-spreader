package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.encryption.api.CryptService;
import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.Objects;

public final class DecryptedAggregateRootEventConsumable implements AggregateRootEventConsumable<JsonNode> {

    private final AggregateRootEventId eventId;

    private final String eventType;

    private final LocalDateTime creationDate;

    private final JsonNode eventPayload;

    private final JsonNode eventMetaData;

    private final JsonNode materializedState;

    private DecryptedAggregateRootEventConsumable(final Builder builder) {
        this.eventId = Objects.requireNonNull(builder.eventId);
        this.eventType = Objects.requireNonNull(builder.eventType);
        this.creationDate = Objects.requireNonNull(builder.creationDate);
        this.eventPayload = Objects.requireNonNull(builder.eventPayload);
        this.eventMetaData = Objects.requireNonNull(builder.eventMetaData);
        this.materializedState = Objects.requireNonNull(builder.materializedState);
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
    public JsonNode eventPayload() {
        return eventPayload;
    }

    @Override
    public JsonNode eventMetaData() {
        return eventMetaData;
    }

    @Override
    public JsonNode materializedState() {
        return materializedState;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private AggregateRootEventId eventId;
        private String eventType;
        private LocalDateTime creationDate;
        private JsonNode eventPayload;
        private JsonNode eventMetaData;
        private JsonNode materializedState;

        private Builder() {}

        public Builder withDebeziumAggregateRootEventConsumable(final DebeziumAggregateRootEventConsumable debeziumAggregateRootEventConsumable) {
            this.eventId = debeziumAggregateRootEventConsumable.eventId();
            this.eventType = debeziumAggregateRootEventConsumable.eventType();
            this.creationDate = debeziumAggregateRootEventConsumable.creationDate();
            this.eventPayload = debeziumAggregateRootEventConsumable.eventPayload();
            this.eventMetaData = debeziumAggregateRootEventConsumable.eventMetaData();
            this.materializedState = debeziumAggregateRootEventConsumable.materializedState();
            return this;
        }

        public DecryptedAggregateRootEventConsumable build(final CryptService<JsonNode> jsonCryptoService,
                                                           final Encryption encryption) {
            jsonCryptoService.recursiveDecrypt(this.eventPayload, encryption);
            jsonCryptoService.recursiveDecrypt(this.eventMetaData, encryption);
            jsonCryptoService.recursiveDecrypt(this.materializedState, encryption);
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
