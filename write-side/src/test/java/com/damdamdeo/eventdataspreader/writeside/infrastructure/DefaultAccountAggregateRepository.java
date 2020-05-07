package com.damdamdeo.eventdataspreader.writeside.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AbstractAggregateRootRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootProjectionRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;

import javax.enterprise.context.Dependent;

@Dependent
public class DefaultAccountAggregateRepository extends AbstractAggregateRootRepository<AccountAggregate> implements AccountAggregateRepository {

    final AggregateRootProjectionRepository aggregateRootProjectionRepository;
    final EventRepository eventRepository;
    final AggregateRootSerializer aggregateRootSerializer;

    public DefaultAccountAggregateRepository(final AggregateRootProjectionRepository aggregateRootProjectionRepository,
                                             final EventRepository eventRepository,
                                             final AggregateRootSerializer aggregateRootSerializer) {
        this.aggregateRootProjectionRepository = aggregateRootProjectionRepository;
        this.eventRepository = eventRepository;
        this.aggregateRootSerializer = aggregateRootSerializer;
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
