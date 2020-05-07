package com.damdamdeo.eventdataspreader.writeside.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AbstractAggregateRootRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootProjectionRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;

import javax.enterprise.context.Dependent;

@Dependent
public class DefaultGiftAggregateRepository extends AbstractAggregateRootRepository<GiftAggregate> implements GiftAggregateRepository {

    final AggregateRootProjectionRepository aggregateRootProjectionRepository;
    final EventRepository eventRepository;
    final AggregateRootSerializer aggregateRootSerializer;

    public DefaultGiftAggregateRepository(final AggregateRootProjectionRepository aggregateRootProjectionRepository,
                                          final EventRepository eventRepository,
                                          final AggregateRootSerializer aggregateRootSerializer) {
        this.aggregateRootProjectionRepository = aggregateRootProjectionRepository;
        this.eventRepository = eventRepository;
        this.aggregateRootSerializer = aggregateRootSerializer;
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
