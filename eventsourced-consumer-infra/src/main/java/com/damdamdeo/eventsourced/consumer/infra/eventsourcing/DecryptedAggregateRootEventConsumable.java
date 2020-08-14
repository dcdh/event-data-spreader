package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.encryption.api.CryptService;
import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Objects;

public final class DecryptedAggregateRootEventConsumable implements AggregateRootEventConsumable<JsonNode> {

    private final AggregateRootEventId eventId;

    private final String eventType;

    private final LocalDateTime creationDate;

    private final JsonNode eventPayload;

    private final JsonNode eventMetaData;

    private final JsonNode materializedState;

    public DecryptedAggregateRootEventConsumable(final AggregateRootEventId eventId,
                                                 final String eventType,
                                                 final LocalDateTime creationDate,
                                                 final JsonNode eventPayload,
                                                 final JsonNode eventMetaData,
                                                 final JsonNode materializedState) {
        this.eventId = Objects.requireNonNull(eventId);
        this.eventType = Objects.requireNonNull(eventType);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.eventPayload = Objects.requireNonNull(eventPayload);
        this.eventMetaData = Objects.requireNonNull(eventMetaData);
        this.materializedState = Objects.requireNonNull(materializedState);
    }

    public DecryptedAggregateRootEventConsumable(final DebeziumAggregateRootEventConsumable debeziumAggregateRootEventConsumable,
                                                 final CryptService<JsonNode> jsonCryptoService,
                                                 final Encryption encryption) {
        this(
                debeziumAggregateRootEventConsumable.eventId(),
                debeziumAggregateRootEventConsumable.eventType(),
                debeziumAggregateRootEventConsumable.creationDate(),
                recursiveDecrypt(debeziumAggregateRootEventConsumable.eventPayload(), jsonCryptoService, encryption),
                recursiveDecrypt(debeziumAggregateRootEventConsumable.eventMetaData(), jsonCryptoService, encryption),
                recursiveDecrypt(debeziumAggregateRootEventConsumable.materializedState(), jsonCryptoService, encryption)
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

    // I should avoid use of recursion in java to avoid StackOverflowError
    // However this exception should not be thrown as json should not be so huge.
    private static JsonNode recursiveDecrypt(final JsonNode jsonNode,
                                             final CryptService<JsonNode> jsonCryptoService,
                                             final Encryption encryption) {
        if (jsonNode.isObject()) {
            final Iterator<String> fieldsNameIterator = jsonNode.fieldNames();
            while (fieldsNameIterator.hasNext()) {
                final String fieldName = fieldsNameIterator.next();
                jsonCryptoService.decrypt(jsonNode, fieldName, encryption);
                recursiveDecrypt(jsonNode.get(fieldName), jsonCryptoService, encryption);
            }
        } else {
            // do nothing
        }
        return jsonNode;
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
