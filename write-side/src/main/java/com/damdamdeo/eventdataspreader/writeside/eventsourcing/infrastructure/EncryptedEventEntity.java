package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventId;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadata;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadataDeserializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadataSerializer;
import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.*;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
import org.apache.commons.lang3.Validate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Table(name = "Event")
@Entity
@NamedQueries({
        @NamedQuery(name = "Events.findEncryptedEventByAggregateRootIdAndAggregateRootTypeOrderByVersionAsc",
                query = "SELECT e FROM EncryptedEventEntity e WHERE e.encryptedIdEventEntity.aggregateRootId = :aggregateRootId " +
                        "AND e.encryptedIdEventEntity.aggregateRootType = :aggregateRootType " +
                        "ORDER BY e.encryptedIdEventEntity.version ASC")
})
public class EncryptedEventEntity implements DecryptableEvent {

    @EmbeddedId
    private EncryptedIdEventEntity encryptedIdEventEntity;

    @NotNull
    private Date creationDate;

    private String eventType;

    @Type(type = "com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.JsonbAsStringUserType")
    @Column(columnDefinition = "jsonb")
    private String eventMetaData;

    @Type(type = "com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.JsonbAsStringUserType")
    @Column(columnDefinition = "jsonb")
    private String eventPayload;

    public EncryptedEventEntity() {}

    private EncryptedEventEntity(final EncryptedEventBuilder builder,
                                 final Optional<EncryptedEventSecret> encryptedEventSecret,
                                 final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                 final EventMetadataSerializer eventMetadataSerializer) {
        this.encryptedIdEventEntity = new EncryptedIdEventEntity(builder.eventId);
        this.eventType = builder.eventType;
        this.creationDate = builder.creationDate;
        this.eventPayload = aggregateRootEventPayloadDeSerializer.serialize(encryptedEventSecret, builder.aggregateRootEventPayload);
        this.eventMetaData = eventMetadataSerializer.serialize(encryptedEventSecret, builder.eventMetaData);
    }

    public static EncryptedEventBuilder newEncryptedEventBuilder() {
        return new EncryptedEventBuilder();
    }

    public static class EncryptedEventBuilder {
        private EventId eventId;
        private String eventType;
        private Date creationDate;
        private AggregateRootEventPayload aggregateRootEventPayload;
        private EventMetadata eventMetaData;

        public EncryptedEventBuilder withEventId(final EventId eventId) {
            this.eventId = eventId;
            return this;
        }

        public EncryptedEventBuilder withEventType(final String eventType) {
            this.eventType = eventType;
            return this;
        }

        public EncryptedEventBuilder withCreationDate(final Date creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public EncryptedEventBuilder withEventPayload(final AggregateRootEventPayload aggregateRootEventPayload) {
            this.aggregateRootEventPayload = aggregateRootEventPayload;
            return this;
        }

        public EncryptedEventBuilder withEventMetaData(final EventMetadata eventMetaData) {
            this.eventMetaData = eventMetaData;
            return this;
        }

        public EncryptedEventEntity build(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                          final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                          final EventMetadataSerializer eventMetadataSerializer) {
            Validate.notNull(eventId);
            Validate.notNull(eventType);
            Validate.notNull(creationDate);
            Validate.notNull(aggregateRootEventPayload);
            Validate.notNull(eventMetaData);
            Validate.notNull(encryptedEventSecret);
            Validate.notNull(aggregateRootEventPayloadDeSerializer);
            Validate.notNull(eventMetadataSerializer);
            Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootId().equals(eventId.aggregateRootId()) : true);
            Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootType().equals(eventId.aggregateRootType()) : true);
            return new EncryptedEventEntity(this, encryptedEventSecret, aggregateRootEventPayloadDeSerializer, eventMetadataSerializer);
        }

    }

    @Override
    public EventId eventId() {
        return encryptedIdEventEntity;
    }

    @Override
    public String eventType() {
        return eventType;
    }

    @Override
    public Date creationDate() {
        return creationDate;
    }

    public String aggregateRootId() {
        return encryptedIdEventEntity.aggregateRootId();
    }

    public String aggregateRootType() {
        return encryptedIdEventEntity.aggregateRootType();
    }

    public Long version() {
        return encryptedIdEventEntity.version();
    }

    @Override
    public AggregateRootEventPayload eventPayload(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                                  final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer) {
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootId().equals(encryptedIdEventEntity.aggregateRootId()) : true);
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootType().equals(encryptedIdEventEntity.aggregateRootType()) : true);
        return aggregateRootEventPayloadDeSerializer.deserialize(encryptedEventSecret, eventPayload);
    }

    @Override
    public EventMetadata eventMetaData(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                       final EventMetadataDeserializer eventMetadataDeserializer) {
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootId().equals(encryptedIdEventEntity.aggregateRootId()) : true);
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootType().equals(encryptedIdEventEntity.aggregateRootType()) : true);
        return eventMetadataDeserializer.deserialize(encryptedEventSecret, eventMetaData);
    }

    public Event toEvent(final Optional<EncryptedEventSecret> encryptedEventSecret,
                         final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                         final EventMetadataDeserializer eventMetadataDeserializer) {
        Validate.notNull(encryptedEventSecret);
        Validate.notNull(aggregateRootEventPayloadDeSerializer);
        Validate.notNull(eventMetadataDeserializer);
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootId().equals(encryptedIdEventEntity.aggregateRootId()) : true);
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootType().equals(encryptedIdEventEntity.aggregateRootType()) : true);
        return new Event(this, encryptedEventSecret, aggregateRootEventPayloadDeSerializer, eventMetadataDeserializer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EncryptedEventEntity)) return false;
        EncryptedEventEntity that = (EncryptedEventEntity) o;
        return Objects.equals(encryptedIdEventEntity, that.encryptedIdEventEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encryptedIdEventEntity);
    }

    @Override
    public String toString() {
        return "EncryptedEventEntity{" +
                "encryptedIdEventEntity=" + encryptedIdEventEntity +
                ", creationDate=" + creationDate +
                ", eventType='" + eventType + '\'' +
                ", eventMetaData='" + eventMetaData + '\'' +
                ", eventPayload='" + eventPayload + '\'' +
                '}';
    }
}
