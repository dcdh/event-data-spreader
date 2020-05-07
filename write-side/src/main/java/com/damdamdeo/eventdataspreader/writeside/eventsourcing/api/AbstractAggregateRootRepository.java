package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

public abstract class AbstractAggregateRootRepository<T extends AggregateRoot> implements AggregateRootRepository<T> {

    @Override
    @Transactional
    public T save(final T aggregateRoot) {
        Objects.requireNonNull(aggregateRoot);
        final EventRepository eventRepository = eventRepository();
        eventRepository.save(aggregateRoot.unsavedEvents());
        aggregateRoot.deleteUnsavedEvents();
        final AggregateRootProjectionRepository aggregateRootProjectionRepository = aggregateRootProjectionRepository();
        aggregateRootProjectionRepository.merge(aggregateRoot);
        return aggregateRoot;
    }

    @Override
    @Transactional
    public T load(final String aggregateRootId) throws UnknownAggregateRootException {
        Objects.requireNonNull(aggregateRootId);
        final EventRepository eventRepository = eventRepository();
        final T instance = createNewInstance();
        final List<Event> events = eventRepository.loadOrderByCreationDateASC(aggregateRootId, instance.getClass().getSimpleName());
        if (events.size() == 0) {
            throw new UnknownAggregateRootException(aggregateRootId);
        }
        instance.loadFromHistory(events);
        return instance;
    }

    protected abstract T createNewInstance();

    protected abstract EventRepository eventRepository();

    protected abstract AggregateRootProjectionRepository aggregateRootProjectionRepository();

}
