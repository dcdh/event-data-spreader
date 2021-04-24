package com.damdamdeo.eventsourced.mutable.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.CryptoService;
import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.eventsourcing.serialization.AggregateRootEventPayloadsDeserializer;
import com.damdamdeo.eventsourced.mutable.eventsourcing.serialization.AggregateRootEventPayloadsSerializer;
import com.damdamdeo.eventsourced.mutable.eventsourcing.serialization.AggregateRootMaterializedStatesDeSerializer;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AggregateRootRepository<T extends AggregateRoot, EVENT_PAYLOAD_INFRA> {

    private final SecretStore secretStore;
    private final Encryption encryption;
    private final AggregateRootInstanceCreator<T> aggregateRootInstanceCreator;
    private final EventRepository<T, EVENT_PAYLOAD_INFRA> eventRepository;
    private final AggregateRootMaterializedStatesDeSerializer<T> aggregateRootMaterializedStatesDeSerializer;
    private final AggregateRootMaterializedStateRepository aggregateRootMaterializedStateRepository;
    private final CryptoService<?, EVENT_PAYLOAD_INFRA> cryptoService;
    private final List<AggregateRootEventPayloadsSerializer> aggregateRootEventPayloadsSerializers;
    private final List<AggregateRootEventPayloadsDeserializer> aggregateRootEventPayloadsDeserializers;

    public AggregateRootRepository(final SecretStore secretStore,
                                   final Encryption encryption,
                                   final AggregateRootInstanceCreator<T> aggregateRootInstanceCreator,
                                   final EventRepository<T, EVENT_PAYLOAD_INFRA> eventRepository,
                                   final AggregateRootMaterializedStatesDeSerializer<T> aggregateRootMaterializedStatesDeSerializer,
                                   final AggregateRootMaterializedStateRepository aggregateRootMaterializedStateRepository,
                                   final CryptoService<?, EVENT_PAYLOAD_INFRA> cryptoService,
                                   final List<AggregateRootEventPayloadsSerializer> aggregateRootEventPayloadsSerializers,
                                   final List<AggregateRootEventPayloadsDeserializer> aggregateRootEventPayloadsDeserializers) {
        this.secretStore = Objects.requireNonNull(secretStore);
        this.encryption = Objects.requireNonNull(encryption);
        this.aggregateRootInstanceCreator = Objects.requireNonNull(aggregateRootInstanceCreator);
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.aggregateRootMaterializedStatesDeSerializer = Objects.requireNonNull(aggregateRootMaterializedStatesDeSerializer);
        this.aggregateRootMaterializedStateRepository = Objects.requireNonNull(aggregateRootMaterializedStateRepository);
        this.cryptoService = Objects.requireNonNull(cryptoService);
        this.aggregateRootEventPayloadsSerializers = Objects.requireNonNull(aggregateRootEventPayloadsSerializers);
        this.aggregateRootEventPayloadsDeserializers = Objects.requireNonNull(aggregateRootEventPayloadsDeserializers);
    }

    @Transactional
    public T save(final T aggregateRoot) throws Exception {
        Objects.requireNonNull(aggregateRoot);
        generateNewSecret(aggregateRoot);
        final T lastSavedAggregateRootState = createAndLoad(aggregateRoot.aggregateRootId());
        aggregateRoot.unsavedEvents()
                .stream()
                .forEach(event -> {
                    final String aggregateRootType = event.aggregateRootId().aggregateRootType();
                    final String eventType = event.eventType();
sinon je fais un mapping de json cryptable ...
@Confidential
sinon je passe par Jackson ... j'avais déjà produit un code pour le faire ...
fait chier !!!!
                    aggregateRootEventPayloadsSerializers.stream()
                            .filter(serializer -> aggregateRootType.equals(serializer.aggregateRootType()))
                            .filter(serializer -> eventType.equals(serializer.eventType()))
                            .findFirst()
                            .map(serializer -> serializer.serialize(event.aggregateRootId(), event.eventPayload()))
                            .orElseThrow(() -> new UnsupportedAggregateRootEventPayload(aggregateRootType, eventType));
sinon je fais le necessaire pour boucler depuis l'aggregat via l'api de reflection
fait chier !!!!

                    // for each event I apply it again from the lastSavedAggregateRootState to get the expected materialized state
                    lastSavedAggregateRootState.apply(event.eventType(), event.eventPayload());
                    lastSavedAggregateRootState.deleteUnsavedEvents();// ensure that the new event created from a known event will be removed before serialization
                    eventRepository.save(event, lastSavedAggregateRootState);
                });
        aggregateRoot.deleteUnsavedEvents();
        final String serializedAggregateRoot = aggregateRootMaterializedStatesDeSerializer.serialize(aggregateRoot, false);
        final AggregateRootMaterializedState aggregateRootMaterializedState = new DefaultAggregateRootMaterializedState(aggregateRoot, serializedAggregateRoot);
        aggregateRootMaterializedStateRepository.persist(aggregateRootMaterializedState);
        return aggregateRoot;
    }

    @Transactional
    public T load(final AggregateRootId aggregateRootId) throws UnknownAggregateRootException {
        Objects.requireNonNull(aggregateRootId);
        final T aggregateRootInstance = aggregateRootInstanceCreator.createNewInstance(aggregateRootId);
        final List<AggregateRootEvent> aggregateRootEvents = loadOrderByVersionASC(aggregateRootId);
        if (aggregateRootEvents.size() == 0) {
            throw new UnknownAggregateRootException(aggregateRootInstance.aggregateRootId());
        }
        aggregateRootInstance.loadFromHistory(aggregateRootEvents);
        return aggregateRootInstance;
    }

    @Transactional
    public T load(final AggregateRootId aggregateRootId, Long version) throws UnknownAggregateRootException {
        Objects.requireNonNull(aggregateRootId);
        Objects.requireNonNull(version);
        final T aggregateRootInstance = aggregateRootInstanceCreator.createNewInstance(aggregateRootId);
        final List<AggregateRootEvent> aggregateRootEvents = loadOrderByVersionASC(aggregateRootId);
        if (aggregateRootEvents.size() == 0) {
            throw new UnknownAggregateRootException(aggregateRootInstance.aggregateRootId());
        }
        aggregateRootInstance.loadFromHistory(aggregateRootEvents);
        return aggregateRootInstance;
    }

    @Transactional
    public T findMaterializedState(final AggregateRootId aggregateRootId) throws UnknownAggregateRootException {
        final AggregateRootMaterializedState aggregateRootMaterializedState = aggregateRootMaterializedStateRepository.find(aggregateRootId);
        return aggregateRootMaterializedStatesDeSerializer.deserialize(aggregateRootMaterializedState);
    }

    private void generateNewSecret(final AggregateRoot aggregateRoot) {
        if (Long.valueOf(0l).equals(aggregateRoot.version())) {
            final String newSecretToStore = encryption.generateNewSecret();
            secretStore.store(aggregateRoot.aggregateRootId(), newSecretToStore);
        }
    }

    private T createAndLoad(final AggregateRootId aggregateRootId) {
        Objects.requireNonNull(aggregateRootId);
        final T aggregateRootInstance = aggregateRootInstanceCreator.createNewInstance(aggregateRootId);
        final List<AggregateRootEvent> aggregateRootEvents = loadOrderByVersionASC(aggregateRootId);
        aggregateRootInstance.loadFromHistory(aggregateRootEvents);
        return aggregateRootInstance;
    }

    private List<AggregateRootEvent> loadOrderByVersionASC(final AggregateRootId aggregateRootId) {
        return eventRepository.loadOrderByVersionASC(aggregateRootId)
                .stream()
                .map(encryptedAggregateRootEvent -> {
                    final EVENT_PAYLOAD_INFRA eventPayload = encryptedAggregateRootEvent.decryptPayload(cryptoService);
                    final AggregateRootEventId aggregateRootEventId = encryptedAggregateRootEvent.aggregateRootEventId();
                    final String aggregateRootType = aggregateRootEventId.aggregateRootId().aggregateRootType();
                    final String eventType = encryptedAggregateRootEvent.eventType();
                    final AggregateRootEventPayload aggregateRootEventPayload = aggregateRootEventPayloadsDeserializers.stream()
                            .filter(deserializer -> aggregateRootType.equals(deserializer.aggregateRootType()))
                            .filter(deserializer -> eventType.equals(deserializer.eventType()))
                            .findFirst()
                            .map(deserializer -> deserializer.deserialize(eventPayload))
                            .orElseThrow(() -> new UnsupportedAggregateRootEventPayload(aggregateRootType, eventType));
                    return new AggregateRootEvent(aggregateRootEventId, eventType, encryptedAggregateRootEvent.creationDate(), aggregateRootEventPayload);
                })
                .collect(Collectors.toList());
    }

}
