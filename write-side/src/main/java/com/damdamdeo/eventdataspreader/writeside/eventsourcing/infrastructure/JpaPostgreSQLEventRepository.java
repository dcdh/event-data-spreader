package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadataDeserializer;
import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.AESEncryption;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadataSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;
import org.apache.commons.lang3.Validate;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Dependent
public class JpaPostgreSQLEventRepository implements EventRepository {

    private static final String AGGREGATE_ROOT_ID = "aggregateRootId";
    private static final String AGGREGATE_ROOT_TYPE = "aggregateRootType";

    final EntityManager entityManager;
    final Encryption encryption;
    final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer;
    final EventMetadataSerializer eventMetadataSerializer;
    final EventMetadataDeserializer eventMetadataDeserializer;

    public JpaPostgreSQLEventRepository(final EntityManager entityManager,
                                        final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                        final EventMetadataSerializer eventMetadataSerializer,
                                        final EventMetadataDeserializer eventMetadataDeserializer) {
        this.entityManager = Objects.requireNonNull(entityManager);
        this.encryption = new AESEncryption();
        this.aggregateRootEventPayloadDeSerializer = aggregateRootEventPayloadDeSerializer;
        this.eventMetadataSerializer = eventMetadataSerializer;
        this.eventMetadataDeserializer = eventMetadataDeserializer;
    }

    @Transactional
    @Override
    public EncryptedEventSecret save(final List<Event> events) {
        Validate.validState(events.size() > 0);
        final String aggregateRootId = events.get(0).aggregateRootId();
        final String aggregateRootType = events.get(0).aggregateRootType();
        Validate.validState(events.stream().allMatch(event -> aggregateRootId.equals(event.aggregateRootId())
                && aggregateRootType.equals(event.aggregateRootType())));
        final EncryptedEventSecret encryptedEventSecret = getEncryptedEventKey(events, encryption)
                .orElseGet(() -> new NoEncryptedEventSecret(aggregateRootId, aggregateRootType));
        events.stream()
                .map(event -> EncryptedEventEntity.newEncryptedEventBuilder()
                        .withEventId(event.eventId())
                        .withAggregateRootId(event.aggregateRootId())
                        .withAggregateRootType(event.aggregateRootType())
                        .withEventType(event.eventType())
                        .withVersion(event.version())
                        .withCreationDate(event.creationDate())
                        .withEventPayload(event.eventPayload())
                        .withEventMetaData(event.eventMetaData())
                        .build(encryptedEventSecret, aggregateRootEventPayloadDeSerializer, eventMetadataSerializer))
                .forEach(eventEntity -> entityManager.persist(eventEntity));
        return encryptedEventSecret;
    }

    private Optional<EncryptedEventSecret> getEncryptedEventKey(final List<Event> events,
                                                      final Encryption encryption) {
        final String aggregateRootId = events.get(0).aggregateRootId();
        final String aggregateRootType = events.get(0).aggregateRootType();

        if (events.get(0).version() == 0L) {
            final EncryptedEventEntity encryptedEventKeyToAdd = EncryptedEventEntity.newEncryptedEventKeyBuilder()
                    .withId(String.format("%30s|%50s|%010d", aggregateRootId, aggregateRootType, -1L).replace(' ', '*'))
                    .withAggregateRootId(aggregateRootId)
                    .withAggregateRootType(aggregateRootType)
                    .withCreationDate(new Date())
                    .withSecret(encryption.generateNewSecret())
                    .build();
            entityManager.persist(encryptedEventKeyToAdd);
            return Optional.of(encryptedEventKeyToAdd);
        }
        return entityManager
                .createNamedQuery("Events.findEncryptedEventSecretByAggregateRootIdAndAggregateRootType", EncryptedEventEntity.class)
                .setParameter(AGGREGATE_ROOT_ID, aggregateRootId)
                .setParameter(AGGREGATE_ROOT_TYPE, aggregateRootType)
                .getResultStream()
                .map(EncryptedEventSecret.class::cast)
                .findFirst();
    }

    @Transactional
    @Override
    public List<Event> load(final String aggregateRootId, final String aggregateRootType) {
        final EncryptedEventSecret encryptedEventSecret = entityManager
                .createNamedQuery("Events.findEncryptedEventSecretByAggregateRootIdAndAggregateRootType", EncryptedEventEntity.class)
                .setParameter(AGGREGATE_ROOT_ID, aggregateRootId)
                .setParameter(AGGREGATE_ROOT_TYPE, aggregateRootType)
                .getResultStream()
                .map(EncryptedEventSecret.class::cast)
                .findFirst()
                .orElseGet(() -> new NoEncryptedEventSecret(aggregateRootId, aggregateRootType));
        return entityManager.createNamedQuery("Events.findEncryptedEventByAggregateRootIdAndAggregateRootTypeOrderByVersionAsc", EncryptedEventEntity.class)
                .setParameter(AGGREGATE_ROOT_ID, aggregateRootId)
                .setParameter(AGGREGATE_ROOT_TYPE, aggregateRootType)
                .getResultStream()
                .map(encryptedEvent -> encryptedEvent.toEvent(encryptedEventSecret, aggregateRootEventPayloadDeSerializer, eventMetadataDeserializer))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    // No Secret due to anonymisation :)
    static final class NoEncryptedEventSecret implements EncryptedEventSecret {

        private final String aggregateRootId;
        private final String aggregateRootType;

        public NoEncryptedEventSecret(final String aggregateRootId, final String aggregateRootType) {
            this.aggregateRootId = aggregateRootId;
            this.aggregateRootType = aggregateRootType;
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
        public Date creationDate() {
            return new Date(0L);
        }

        @Override
        public String secret() {
            return null;
        }

    }

}
