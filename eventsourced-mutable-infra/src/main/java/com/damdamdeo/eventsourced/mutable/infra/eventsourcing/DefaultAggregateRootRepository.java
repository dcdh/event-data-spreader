package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

import com.damdamdeo.eventsourced.encryption.api.*;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.eventsourcing.*;
import com.damdamdeo.eventsourced.mutable.eventsourcing.serialization.AggregateRootMaterializedStatesDeSerializer;

FCK extraire je dois en faire
@ApplicationScoped
public class DefaultAggregateRootRepository implements AggregateRootRepository {

    private final EventRepository eventRepository;

    private final AggregateRootMaterializedStateRepository aggregateRootMaterializedStateRepository;

    private final AggregateRootMaterializedStatesDeSerializer aggregateRootMaterializedStatesDeSerializer;

    private final SecretStore secretStore;

    private final Encryption aesEncryption;

    private final AggregateRootInstanceCreator aggregateRootInstanceCreator;

    public DefaultAggregateRootRepository(final EventRepository eventRepository,
                                          final AggregateRootMaterializedStateRepository aggregateRootMaterializedStateRepository,
                                          final AggregateRootMaterializedStatesDeSerializer aggregateRootMaterializedStatesDeSerializer,
                                          final SecretStore secretStore,
                                          @AESEncryptionQualifier final Encryption aesEncryption,
                                          final AggregateRootInstanceCreator aggregateRootInstanceCreator) {
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.aggregateRootMaterializedStateRepository = Objects.requireNonNull(aggregateRootMaterializedStateRepository);
        this.aggregateRootMaterializedStatesDeSerializer = Objects.requireNonNull(aggregateRootMaterializedStatesDeSerializer);
        this.secretStore = Objects.requireNonNull(secretStore);
        this.aesEncryption = Objects.requireNonNull(aesEncryption);
        this.aggregateRootInstanceCreator = Objects.requireNonNull(aggregateRootInstanceCreator);
    }

    @Override
    @Transactional
    public <T extends AggregateRoot> T save(final T aggregateRoot) {
        Objects.requireNonNull(aggregateRoot);
        generateNewSecret(aggregateRoot, aesEncryption);
        final AggregateRoot lastSavedAggregateRootState = createAndLoad(aggregateRoot.aggregateRootId(), aggregateRoot.getClass());
        aggregateRoot.unsavedEvents()
                .stream()
                .forEach(event -> {
                    // for each event I apply it again from the lastSavedAggregateRootState to get the expected materialized state
                    lastSavedAggregateRootState.apply(event.eventType(), event.eventPayload());
                    lastSavedAggregateRootState.deleteUnsavedEvents();// ensure that the new event created from a known event will be removed before serialization
                    eventRepository.save(event, lastSavedAggregateRootState);
                });
        aggregateRoot.deleteUnsavedEvents();
        final String serializedAggregateRoot = aggregateRootMaterializedStatesDeSerializer.serialize(aggregateRoot, false);
        final DefaultAggregateRootMaterializedState defaultAggregateRootMaterializedState = new DefaultAggregateRootMaterializedState(aggregateRoot, serializedAggregateRoot);
        aggregateRootMaterializedStateRepository.persist(defaultAggregateRootMaterializedState);
        return aggregateRoot;
    }

    <T extends AggregateRoot> T createAndLoad(final AggregateRootId aggregateRootId, final Class<T> clazz) {
        Objects.requireNonNull(aggregateRootId);
        Objects.requireNonNull(clazz);
        final T instance = aggregateRootInstanceCreator.createNewInstance(clazz, aggregateRootId.aggregateRootId());
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC(aggregateRootId);
        instance.loadFromHistory(aggregateRootEvents);
        return instance;
    }

    @Override
    @Transactional
    public <T extends AggregateRoot> T load(final String aggregateRootId, final Class<T> clazz) throws UnknownAggregateRootException {
        Objects.requireNonNull(aggregateRootId);
        Objects.requireNonNull(clazz);
        final T instance = aggregateRootInstanceCreator.createNewInstance(clazz, aggregateRootId);
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC(instance.aggregateRootId());
        if (aggregateRootEvents.size() == 0) {
            throw new UnknownAggregateRootException(instance.aggregateRootId());
        }
        instance.loadFromHistory(aggregateRootEvents);
        return instance;
    }

    @Override
    @Transactional
    public <T extends AggregateRoot> T load(final String aggregateRootId, final Class<T> clazz, final Long version) throws UnknownAggregateRootException {
        Objects.requireNonNull(aggregateRootId);
        Objects.requireNonNull(clazz);
        final T instance = aggregateRootInstanceCreator.createNewInstance(clazz, aggregateRootId);
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC(instance.aggregateRootId(), version);
        if (aggregateRootEvents.size() == 0) {
            throw new UnknownAggregateRootException(instance.aggregateRootId());
        }
        instance.loadFromHistory(aggregateRootEvents);
        return instance;
    }

    @Override
    public <T extends AggregateRoot> T findMaterializedState(final String aggregateRootId, final Class<T> clazz) throws UnknownAggregateRootException {
        final AggregateRootMaterializedState aggregateRootMaterializedState = aggregateRootMaterializedStateRepository.find(new ApiAggregateRootId(aggregateRootId, clazz));
        return aggregateRootMaterializedStatesDeSerializer.deserialize(aggregateRootMaterializedState);
    }

    private void generateNewSecret(final AggregateRoot aggregateRoot,
                                   final Encryption encryption) {
        if (Long.valueOf(0l).equals(aggregateRoot.version())) {
            final String newSecretToStore = encryption.generateNewSecret();
            secretStore.store(aggregateRoot.aggregateRootId(), newSecretToStore);
        }
    }

}
