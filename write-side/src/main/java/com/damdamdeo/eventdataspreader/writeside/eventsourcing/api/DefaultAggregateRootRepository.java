package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class DefaultAggregateRootRepository implements AggregateRootRepository {

    private final EventRepository eventRepository;

    private final AggregateRootProjectionRepository aggregateRootProjectionRepository;

    public DefaultAggregateRootRepository(final EventRepository eventRepository,
                                          final AggregateRootProjectionRepository aggregateRootProjectionRepository) {
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.aggregateRootProjectionRepository = Objects.requireNonNull(aggregateRootProjectionRepository);
    }

    @Override
    @Transactional
    public <T extends AggregateRoot> T save(final T aggregateRoot) {
        Objects.requireNonNull(aggregateRoot);
        eventRepository.save(aggregateRoot.unsavedEvents());
        aggregateRoot.deleteUnsavedEvents();
        aggregateRootProjectionRepository.merge(aggregateRoot);
        return aggregateRoot;
    }

    @Override
    @Transactional
    public <T extends AggregateRoot> T load(String aggregateRootId, Class<T> clazz) throws UnknownAggregateRootException {
        Objects.requireNonNull(aggregateRootId);
        final T instance = createNewInstance(clazz);
        final List<Event> events = eventRepository.loadOrderByVersionASC(aggregateRootId, instance.getClass().getSimpleName());
        if (events.size() == 0) {
            throw new UnknownAggregateRootException(aggregateRootId);
        }
        instance.loadFromHistory(events);
        return instance;
    }

    <T extends AggregateRoot> T createNewInstance(final Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
