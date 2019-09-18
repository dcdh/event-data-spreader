package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootProjection;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootProjectionRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class JpaPostgreSQLAggregateRootProjectionRepository implements AggregateRootProjectionRepository {

    final EntityManager em;

    public JpaPostgreSQLAggregateRootProjectionRepository(final EntityManager em) {
        this.em = Objects.requireNonNull(em);
    }

    @Override
    @Transactional
    public AggregateRootProjection save(final AggregateRootProjection aggregateRootProjection) {
        final AggregateRootProjectionEntity aggregateRootProjectionEntity = new AggregateRootProjectionEntity(aggregateRootProjection);
        return Optional.of(em.merge(aggregateRootProjectionEntity))
                .map(AggregateRootProjectionEntity::toAggregateRootProjection)
                .get();
    }

}
