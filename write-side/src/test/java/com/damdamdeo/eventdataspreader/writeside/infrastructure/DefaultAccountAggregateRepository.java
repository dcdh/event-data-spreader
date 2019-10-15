package com.damdamdeo.eventdataspreader.writeside.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AbstractAggregateRootRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootProjectionRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;

@Dependent
public class DefaultAccountAggregateRepository extends AbstractAggregateRootRepository<AccountAggregate> implements AccountAggregateRepository {

    final EntityManager entityManager;
    final EventRepository eventRepository;
    final AggregateRootProjectionRepository aggregateRootProjectionRepository;

    public DefaultAccountAggregateRepository(final EntityManager entityManager,
                                             final EventRepository eventRepository,
                                             final AggregateRootProjectionRepository aggregateRootProjectionRepository) {
        this.entityManager = entityManager;
        this.eventRepository = eventRepository;
        this.aggregateRootProjectionRepository = aggregateRootProjectionRepository;
    }

    @Override
    protected AccountAggregate createNewInstance() {
        return new AccountAggregate();
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
