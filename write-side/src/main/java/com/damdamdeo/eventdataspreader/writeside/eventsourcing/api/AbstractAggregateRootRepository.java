package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.AggregateRootEntity;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

public abstract class AbstractAggregateRootRepository<T extends AggregateRoot> implements AggregateRootRepository<T> {

    @Override
    @Transactional
    public T save(final T aggregateRoot) {
        Objects.requireNonNull(aggregateRoot);
        final EventRepository eventRepository = eventRepository();
        final EncryptedEventSecret encryptedEventSecret = eventRepository.save(aggregateRoot.unsavedEvents());
        aggregateRoot.deleteUnsavedEvents();
        final EntityManager entityManager = entityManager();
        final AggregateRootSerializer aggregateRootSerializer = aggregateRootSerializer();
        entityManager.merge(new AggregateRootEntity(aggregateRoot, aggregateRootSerializer, encryptedEventSecret));
        return aggregateRoot;
    }

    @Override
    @Transactional
    public T load(final String aggregateRootId) throws UnknownAggregateRootException {
        Objects.requireNonNull(aggregateRootId);
        final EventRepository eventRepository = eventRepository();
        final T instance = createNewInstance();
        final List<Event> events = eventRepository.load(aggregateRootId, instance.getClass().getSimpleName());
        if (events.size() == 0) {
            throw new UnknownAggregateRootException(aggregateRootId);
        }
        instance.loadFromHistory(events);
        return instance;
    }

    protected abstract T createNewInstance();

    protected abstract EventRepository eventRepository();

    protected abstract EntityManager entityManager();

    protected abstract AggregateRootSerializer aggregateRootSerializer();

}
