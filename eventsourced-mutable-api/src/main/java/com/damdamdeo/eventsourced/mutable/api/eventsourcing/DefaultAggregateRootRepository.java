package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;

// TODO c'est de l'infra
@ApplicationScoped
public class DefaultAggregateRootRepository implements AggregateRootRepository {

    private final EventRepository eventRepository;

    private final AggregateRootMaterializedStateRepository aggregateRootMaterializedStateRepository;

    private final AggregateRootMaterializedStateSerializer aggregateRootMaterializedStateSerializer;

    private final SecretStore secretStore;

    private final Encryption encryption;

    public DefaultAggregateRootRepository(final EventRepository eventRepository,
                                          final AggregateRootMaterializedStateRepository aggregateRootMaterializedStateRepository,
                                          final AggregateRootMaterializedStateSerializer aggregateRootMaterializedStateSerializer,
                                          final SecretStore secretStore,
                                          final Encryption encryption) {
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.aggregateRootMaterializedStateRepository = Objects.requireNonNull(aggregateRootMaterializedStateRepository);
        this.aggregateRootMaterializedStateSerializer = Objects.requireNonNull(aggregateRootMaterializedStateSerializer);
        this.secretStore = Objects.requireNonNull(secretStore);
        this.encryption = Objects.requireNonNull(encryption);
    }

    @Override
    @Transactional
    public <T extends AggregateRoot> T save(final T aggregateRoot) {
        Objects.requireNonNull(aggregateRoot);
        final Optional<AggregateRootSecret> aggregateRootSecret = getAggregateRootSecret(aggregateRoot, encryption);
        eventRepository.save(aggregateRoot.unsavedEvents(), aggregateRootSecret);
        aggregateRoot.unsavedEvents().stream()
                .forEach(event -> {
                    final AggregateRoot aggregateRootToMaterialize = load(aggregateRoot.aggregateRootId().aggregateRootId(), aggregateRoot.getClass(), event.version());
                    eventRepository.saveMaterializedState(aggregateRootToMaterialize, aggregateRootSecret);
                });
        aggregateRoot.deleteUnsavedEvents();
        final String serializedAggregateRoot = aggregateRootMaterializedStateSerializer.serialize(aggregateRoot);
        final DefaultAggregateRootMaterializedState defaultAggregateRootMaterializedState = new DefaultAggregateRootMaterializedState(aggregateRoot, serializedAggregateRoot);
        aggregateRootMaterializedStateRepository.persist(defaultAggregateRootMaterializedState);
        return aggregateRoot;
    }

    @Override
    @Transactional
    public <T extends AggregateRoot> T load(final String aggregateRootId, final Class<T> clazz) throws UnknownAggregateRootException {
        Objects.requireNonNull(aggregateRootId);
        Objects.requireNonNull(clazz);
        final T instance = createNewInstance(clazz);
        final String aggregateRootType = instance.getClass().getSimpleName();
        final Optional<AggregateRootSecret> aggregateRootSecret = Optional.ofNullable(secretStore.read(aggregateRootType, aggregateRootId));
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC(aggregateRootId, aggregateRootType, aggregateRootSecret);
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
        final T instance = createNewInstance(clazz);
        final String aggregateRootType = instance.getClass().getSimpleName();
        final Optional<AggregateRootSecret> aggregateRootSecret = Optional.ofNullable(secretStore.read(aggregateRootType, aggregateRootId));
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC(aggregateRootId, aggregateRootType, aggregateRootSecret, version);
        if (aggregateRootEvents.size() == 0) {
            throw new UnknownAggregateRootException(aggregateRootId);
        }
        instance.loadFromHistory(aggregateRootEvents);
        return instance;
    }

    <T extends AggregateRoot> T createNewInstance(final Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<AggregateRootSecret> getAggregateRootSecret(final AggregateRoot aggregateRoot,
                                                                 final Encryption encryption) {
        final String aggregateRootId = aggregateRoot.aggregateRootId().aggregateRootId();
        final String aggregateRootType = aggregateRoot.aggregateRootId().aggregateRootType();
        if (aggregateRoot.version() == 0L) {
            final String newSecretToStore = encryption.generateNewSecret();
            return Optional.of(secretStore.store(aggregateRootType,
                    aggregateRootId,
                    newSecretToStore));
        }
        return Optional.of(secretStore.read(aggregateRootType, aggregateRootId));
    }

}
