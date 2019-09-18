package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.GiftAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AbstractAggregateRootRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootProjectionRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;

@ApplicationScoped
public class DefaultGiftAggregateRepository extends AbstractAggregateRootRepository<GiftAggregate> implements GiftAggregateRepository {

    final EntityManager entityManager;
    final EventRepository eventRepository;
    final AggregateRootProjectionRepository aggregateRootProjectionRepository;

    public DefaultGiftAggregateRepository(final EntityManager entityManager,
                                          final EventRepository eventRepository,
                                          final AggregateRootProjectionRepository aggregateRootProjectionRepository) {
        this.entityManager = entityManager;
        this.eventRepository = eventRepository;
        this.aggregateRootProjectionRepository = aggregateRootProjectionRepository;
    }

    @Override
    protected GiftAggregate createNewInstance() {
        return new GiftAggregate();
    }

    @Override
    protected EventRepository eventRepository() {
        return eventRepository;
    }

    @Override
    protected AggregateRootProjectionRepository aggregateRootProjectionRepository() {
        return aggregateRootProjectionRepository;
    }

}
