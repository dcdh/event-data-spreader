package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

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

@Table(name = "Event")
@Entity
@NamedQueries({
        @NamedQuery(name = "Events.findEncryptedEventByAggregateRootIdAndAggregateRootTypeOrderByVersionAsc",
                query = "SELECT e FROM EncryptedEventEntity e WHERE e.encryptedEventType = com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventType.ENCRYPTED_EVENT AND " +
                        "e.aggregateRootId = :aggregateRootId AND e.aggregateRootType = :aggregateRootType ORDER BY e.version ASC"),
        @NamedQuery(name = "Events.findEncryptedEventSecretByAggregateRootIdAndAggregateRootType",
                query = "SELECT e FROM EncryptedEventEntity e WHERE e.encryptedEventType = com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventType.SECRET AND " +
                        "e.aggregateRootId = :aggregateRootId AND e.aggregateRootType = :aggregateRootType")
})
public class EncryptedEventEntity implements DecryptableEvent, EncryptedEventSecret {

    @Id
    private String id;

    @NotNull
    private String aggregateRootId;

    @NotNull
    private String aggregateRootType;

    @NotNull
    private Long version;

    @NotNull
    private Date creationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EncryptedEventType encryptedEventType;

    // Encryption
    private String secret;

    // Domain Event
    private String eventType;

    @Type(type = "com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.JsonbAsStringUserType")
    @Column(columnDefinition = "jsonb")
    private String eventMetaData;

    @Type(type = "com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.JsonbAsStringUserType")
    @Column(columnDefinition = "jsonb")
    private String eventPayload;

    public EncryptedEventEntity() {}

    private EncryptedEventEntity(final String id,
                                 final String aggregateRootId,
                                 final String aggregateRootType,
                                 final Long version) {
        this.id = id;
        this.aggregateRootId = aggregateRootId;
        this.aggregateRootType = aggregateRootType;
        this.version = version;
    }

    private EncryptedEventEntity(final EncryptedEventBuilder builder,
                                 final EncryptedEventSecret encryptedEventSecret,
                                 final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                 final EventMetadataSerializer eventMetadataSerializer) {
        this(builder.eventId, builder.aggregateRootId, builder.aggregateRootType, builder.version);
        this.eventType = builder.eventType;
        this.creationDate = builder.creationDate;
        this.eventPayload = aggregateRootEventPayloadDeSerializer.serialize(encryptedEventSecret, builder.aggregateRootEventPayload);
        this.eventMetaData = eventMetadataSerializer.serialize(encryptedEventSecret, builder.eventMetaData);
        this.encryptedEventType = EncryptedEventType.ENCRYPTED_EVENT;
    }

    private EncryptedEventEntity(final EncryptedEventKeyBuilder builder) {
        this(builder.id, builder.aggregateRootId, builder.aggregateRootType, -1L);
        this.creationDate = builder.creationDate;
        this.secret = builder.secret;
        this.encryptedEventType = EncryptedEventType.SECRET;
    }

    public static EncryptedEventBuilder newEncryptedEventBuilder() {
        return new EncryptedEventBuilder();
    }

    public static class EncryptedEventBuilder {
        private String eventId;
        private String aggregateRootId;
        private String aggregateRootType;
        private String eventType;
        private Long version;
        private Date creationDate;
        private AggregateRootEventPayload aggregateRootEventPayload;
        private EventMetadata eventMetaData;

        public EncryptedEventBuilder withEventId(final String eventId) {
            this.eventId = eventId;
            return this;
        }

        public EncryptedEventBuilder withAggregateRootId(final String aggregateRootId) {
            this.aggregateRootId = aggregateRootId;
            return this;
        }

        public EncryptedEventBuilder withAggregateRootType(final String aggregateRootType) {
            this.aggregateRootType = aggregateRootType;
            return this;
        }

        public EncryptedEventBuilder withEventType(final String eventType) {
            this.eventType = eventType;
            return this;
        }

        public EncryptedEventBuilder withVersion(final Long version) {
            this.version = version;
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

        public EncryptedEventEntity build(final EncryptedEventSecret encryptedEventSecret,
                                          final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                          final EventMetadataSerializer eventMetadataSerializer) {
            Validate.notNull(eventId);
            Validate.notNull(aggregateRootId);
            Validate.notNull(aggregateRootType);
            Validate.notNull(eventType);
            Validate.notNull(version);
            Validate.notNull(creationDate);
            Validate.notNull(aggregateRootEventPayload);
            Validate.notNull(eventMetaData);
            Validate.notNull(encryptedEventSecret);
            Validate.notNull(aggregateRootEventPayloadDeSerializer);
            Validate.notNull(eventMetadataSerializer);
            Validate.validState(encryptedEventSecret.aggregateRootId().equals(aggregateRootId));
            Validate.validState(encryptedEventSecret.aggregateRootType().equals(aggregateRootType));
            return new EncryptedEventEntity(this, encryptedEventSecret, aggregateRootEventPayloadDeSerializer, eventMetadataSerializer);
        }

    }

    public static EncryptedEventKeyBuilder newEncryptedEventKeyBuilder() {
        return new EncryptedEventKeyBuilder();
    }

    public static class EncryptedEventKeyBuilder {
        private String id;
        private String aggregateRootId;
        private String aggregateRootType;
        private Date creationDate;
        private String secret;

        public EncryptedEventKeyBuilder withId(final String id) {
            this.id = id;
            return this;
        }

        public EncryptedEventKeyBuilder withAggregateRootId(final String aggregateRootId) {
            this.aggregateRootId = aggregateRootId;
            return this;
        }

        public EncryptedEventKeyBuilder withAggregateRootType(final String aggregateRootType) {
            this.aggregateRootType = aggregateRootType;
            return this;
        }

        public EncryptedEventKeyBuilder withCreationDate(final Date creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public EncryptedEventKeyBuilder withSecret(final String secret) {
            this.secret = secret;
            return this;
        }

        public EncryptedEventEntity build() {
            Validate.notNull(id);
            Validate.notNull(aggregateRootId);
            Validate.notNull(aggregateRootType);
            Validate.notNull(creationDate);
            Validate.notNull(secret);
            return new EncryptedEventEntity(this);
        }
    }

    @Override
    public String eventId() {
        return id;
    }

    @Override
    public String aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public String aggregateRootType() {
        return aggregateRootType;
    }

    @Override
    public String secret() {
        return secret;
    }

    @Override
    public String eventType() {
        return eventType;
    }

    @Override
    public Long version() {
        return version;
    }

    @Override
    public Date creationDate() {
        return creationDate;
    }

    @Override
    public AggregateRootEventPayload eventPayload(final EncryptedEventSecret encryptedEventSecret,
                                                  final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer) {
        Validate.validState(encryptedEventSecret.aggregateRootId().equals(aggregateRootId));
        Validate.validState(encryptedEventSecret.aggregateRootType().equals(aggregateRootType));
        return aggregateRootEventPayloadDeSerializer.deserialize(encryptedEventSecret, eventPayload);
    }

    @Override
    public EventMetadata eventMetaData(final EncryptedEventSecret encryptedEventSecret,
                                       final EventMetadataDeserializer eventMetadataDeserializer) {
        Validate.validState(encryptedEventSecret.aggregateRootId().equals(aggregateRootId));
        Validate.validState(encryptedEventSecret.aggregateRootType().equals(aggregateRootType));
        return eventMetadataDeserializer.deserialize(encryptedEventSecret, eventMetaData);
    }

    public Event toEvent(final EncryptedEventSecret encryptedEventSecret,
                         final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                         final EventMetadataDeserializer eventMetadataDeserializer) {
        Validate.notNull(encryptedEventSecret);
        Validate.notNull(aggregateRootEventPayloadDeSerializer);
        Validate.notNull(eventMetadataDeserializer);
        Validate.validState(encryptedEventSecret.aggregateRootId().equals(aggregateRootId));
        Validate.validState(encryptedEventSecret.aggregateRootType().equals(aggregateRootType));
        Validate.validState(EncryptedEventType.ENCRYPTED_EVENT.equals(encryptedEventType));
        return new Event(this, encryptedEventSecret, aggregateRootEventPayloadDeSerializer, eventMetadataDeserializer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EncryptedEventEntity)) return false;
        EncryptedEventEntity that = (EncryptedEventEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EncryptedEventEntity{" +
                "id='" + id + '\'' +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                ", version=" + version +
                ", creationDate=" + creationDate +
                ", encryptedEventType=" + encryptedEventType +
                ", secret='" + secret + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventMetaData='" + eventMetaData + '\'' +
                ", eventPayload='" + eventPayload + '\'' +
                '}';
    }

}
