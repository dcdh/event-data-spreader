package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.encryption.api.UnsupportedSecret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.*;

@ApplicationScoped
public class DefaultAggregateRootRepository implements AggregateRootRepository {

    private final EventRepository eventRepository;

    private final AggregateRootMaterializedStateRepository aggregateRootMaterializedStateRepository;

    private final AggregateRootMaterializedStateSerializer aggregateRootMaterializedStateSerializer;

    private final SecretStore secretStore;

    private final Encryption encryption;

    private final AggregateRootInstanceCreator aggregateRootInstanceCreator;

    public DefaultAggregateRootRepository(final EventRepository eventRepository,
                                          final AggregateRootMaterializedStateRepository aggregateRootMaterializedStateRepository,
                                          final AggregateRootMaterializedStateSerializer aggregateRootMaterializedStateSerializer,
                                          final SecretStore secretStore,
                                          final Encryption encryption,
                                          final AggregateRootInstanceCreator aggregateRootInstanceCreator) {
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.aggregateRootMaterializedStateRepository = Objects.requireNonNull(aggregateRootMaterializedStateRepository);
        this.aggregateRootMaterializedStateSerializer = Objects.requireNonNull(aggregateRootMaterializedStateSerializer);
        this.secretStore = Objects.requireNonNull(secretStore);
        this.encryption = Objects.requireNonNull(encryption);
        this.aggregateRootInstanceCreator = Objects.requireNonNull(aggregateRootInstanceCreator);
    }

    @Override
    @Transactional
    public <T extends AggregateRoot> T save(final T aggregateRoot) {
        Objects.requireNonNull(aggregateRoot);
        final Secret secret = getSecret(aggregateRoot, encryption);
        final AggregateRoot lastSavedAggregateRootState = createAndLoad(aggregateRoot.aggregateRootId().aggregateRootId(), aggregateRoot.getClass(), secret);
        aggregateRoot.unsavedEvents()
                .stream()
                .forEach(event -> {
                    // for each event I apply it again from the lastSavedAggregateRootState to get the expected materialized state
                    lastSavedAggregateRootState.apply(event.eventType(), event.eventPayload(), event.eventMetaData());
                    lastSavedAggregateRootState.deleteUnsavedEvents();// ensure that the new event created from a known event will be removed before serialization
                    eventRepository.save(event, lastSavedAggregateRootState, secret);
                });
        aggregateRoot.deleteUnsavedEvents();
        final String serializedAggregateRoot = aggregateRootMaterializedStateSerializer.serialize(new UnsupportedSecret(), aggregateRoot);
        final DefaultAggregateRootMaterializedState defaultAggregateRootMaterializedState = new DefaultAggregateRootMaterializedState(aggregateRoot, serializedAggregateRoot);
        aggregateRootMaterializedStateRepository.persist(defaultAggregateRootMaterializedState);
        return aggregateRoot;
    }

    <T extends AggregateRoot> T createAndLoad(final String aggregateRootId, final Class<T> clazz, final Secret secret) {
        Objects.requireNonNull(aggregateRootId);
        Objects.requireNonNull(clazz);
        final T instance = aggregateRootInstanceCreator.createNewInstance(clazz);
        final String aggregateRootType = instance.getClass().getSimpleName();
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC(aggregateRootId, aggregateRootType, secret);
        instance.loadFromHistory(aggregateRootEvents);
        return instance;
    }

    @Override
    @Transactional
    public <T extends AggregateRoot> T load(final String aggregateRootId, final Class<T> clazz) throws UnknownAggregateRootException {
        Objects.requireNonNull(aggregateRootId);
        Objects.requireNonNull(clazz);
        final T instance = aggregateRootInstanceCreator.createNewInstance(clazz);
        final String aggregateRootType = instance.getClass().getSimpleName();
        final Secret secret = secretStore.read(aggregateRootType, aggregateRootId);
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC(aggregateRootId, aggregateRootType, secret);
        if (aggregateRootEvents.size() == 0) {
            throw new UnknownAggregateRootException(aggregateRootId);
        }
        instance.loadFromHistory(aggregateRootEvents);
        return instance;
    }

    @Override
    public <T extends AggregateRoot> T load(final String aggregateRootId, final Class<T> clazz, final Long version) throws UnknownAggregateRootException {
        Objects.requireNonNull(aggregateRootId);
        Objects.requireNonNull(clazz);
        final T instance = aggregateRootInstanceCreator.createNewInstance(clazz);
        final String aggregateRootType = instance.getClass().getSimpleName();
        final Secret secret = secretStore.read(aggregateRootType, aggregateRootId);
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC(aggregateRootId, aggregateRootType, secret, version);
        if (aggregateRootEvents.size() == 0) {
            throw new UnknownAggregateRootException(aggregateRootId);
        }
        instance.loadFromHistory(aggregateRootEvents);
        return instance;
    }

    private Secret getSecret(final AggregateRoot aggregateRoot,
                             final Encryption encryption) {
        final String aggregateRootId = aggregateRoot.aggregateRootId().aggregateRootId();
        final String aggregateRootType = aggregateRoot.aggregateRootId().aggregateRootType();
        if (aggregateRoot.version() == 0L) {
            final String newSecretToStore = encryption.generateNewSecret();
            return secretStore.store(aggregateRootType,
                    aggregateRootId,
                    newSecretToStore);
        }
        return secretStore.read(aggregateRootType, aggregateRootId);
    }

}
