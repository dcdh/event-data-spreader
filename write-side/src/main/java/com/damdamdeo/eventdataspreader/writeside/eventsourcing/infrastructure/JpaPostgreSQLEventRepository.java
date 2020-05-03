package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.EventMetadataDeserializer;
import com.damdamdeo.eventdataspreader.event.api.EventMetadataSerializer;
import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.AESEncryption;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
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
    final SecretStore secretStore;
    final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer;
    final EventMetadataSerializer eventMetadataSerializer;
    final EventMetadataDeserializer eventMetadataDeserializer;

    public JpaPostgreSQLEventRepository(final EntityManager entityManager,
                                        final SecretStore secretStore,
                                        final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                        final EventMetadataSerializer eventMetadataSerializer,
                                        final EventMetadataDeserializer eventMetadataDeserializer) {
        this.entityManager = Objects.requireNonNull(entityManager);
        this.encryption = new AESEncryption();
        this.secretStore = secretStore;
        this.aggregateRootEventPayloadDeSerializer = aggregateRootEventPayloadDeSerializer;
        this.eventMetadataSerializer = eventMetadataSerializer;
        this.eventMetadataDeserializer = eventMetadataDeserializer;
    }

    @Transactional
    @Override
    public void save(final List<Event> events) {
        Validate.validState(events.size() > 0);
        final String aggregateRootId = events.get(0).aggregateRootId();
        final String aggregateRootType = events.get(0).aggregateRootType();
        Validate.validState(events.stream().allMatch(event -> aggregateRootId.equals(event.aggregateRootId())
                && aggregateRootType.equals(event.aggregateRootType())));
        final Optional<EncryptedEventSecret> encryptedEventSecret = getEncryptedEventKey(events, encryption);
        events.stream()
                .map(event -> EncryptedEventEntity.newEncryptedEventBuilder()
                        .withEventId(event.eventId())
                        .withEventType(event.eventType())
                        .withCreationDate(event.creationDate())
                        .withEventPayload(event.eventPayload())
                        .withEventMetaData(event.eventMetaData())
                        .build(encryptedEventSecret, aggregateRootEventPayloadDeSerializer, eventMetadataSerializer))
                .forEach(eventEntity -> entityManager.persist(eventEntity));
    }

    private Optional<EncryptedEventSecret> getEncryptedEventKey(final List<Event> events,
                                                                final Encryption encryption) {
        final String aggregateRootType = events.get(0).aggregateRootType();
        final String aggregateRootId = events.get(0).aggregateRootId();

        if (events.get(0).version() == 0L) {
            final String newSecretToStore = encryption.generateNewSecret();
            final EncryptedEventSecret newEncryptedEventSecret = secretStore.store(aggregateRootType, aggregateRootId, newSecretToStore);
            return Optional.of(newEncryptedEventSecret);
        }
        return secretStore.read(aggregateRootType, aggregateRootId);
    }

    @Transactional
    @Override
    public List<Event> load(final String aggregateRootId, final String aggregateRootType) {
        final Optional<EncryptedEventSecret> encryptedEventSecret = secretStore.read(aggregateRootType,aggregateRootId);
        return entityManager.createNamedQuery("Events.findEncryptedEventByAggregateRootIdAndAggregateRootTypeOrderByVersionAsc", EncryptedEventEntity.class)
                .setParameter(AGGREGATE_ROOT_ID, aggregateRootId)
                .setParameter(AGGREGATE_ROOT_TYPE, aggregateRootType)
                .getResultStream()
                .map(encryptedEvent -> encryptedEvent.toEvent(encryptedEventSecret, aggregateRootEventPayloadDeSerializer, eventMetadataDeserializer))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

}
