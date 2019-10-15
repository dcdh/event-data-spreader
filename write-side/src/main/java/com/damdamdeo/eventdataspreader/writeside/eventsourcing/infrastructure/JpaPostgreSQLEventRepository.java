package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Dependent
public class JpaPostgreSQLEventRepository implements EventRepository {

    final EntityManager entityManager;

    public JpaPostgreSQLEventRepository(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Transactional
    @Override
    public void save(final List<Event> events) {
        events.stream()
                .map(event -> new EventEntity(event))
                .forEach(eventEntity -> entityManager.persist(eventEntity));
    }

    @Transactional
    @Override
    public List<Event> load(final String aggregateRootId, final String aggregateRootType) {
        return entityManager.createNamedQuery("Events.findByAggregateRootIdOrderByVersionAsc", EventEntity.class)
                .setParameter("aggregateRootId", aggregateRootId)
                .setParameter("aggregateRootType", aggregateRootType)
                .getResultStream()
                .map(EventEntity::toEvent)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

}
