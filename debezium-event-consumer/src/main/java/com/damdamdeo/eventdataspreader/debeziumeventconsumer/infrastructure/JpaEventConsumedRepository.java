package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumedRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.*;

@ApplicationScoped
public class JpaEventConsumedRepository implements EventConsumedRepository {

    final EntityManager entityManager;

    public JpaEventConsumedRepository(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    @Transactional
    public void addEventConsumerConsumed(final UUID eventId, final Class consumerClass) {
        EventConsumedEntity eventConsumedEntity;
        try {
            eventConsumedEntity = entityManager.createNamedQuery("Events.findByEventId", EventConsumedEntity.class)
                    .setParameter("eventId", eventId)
                    .getSingleResult();
        } catch (final NoResultException e) {
            eventConsumedEntity = new EventConsumedEntity(eventId);
        }
        eventConsumedEntity.addNewEventConsumerConsumed(consumerClass, new Date());
        entityManager.persist(eventConsumedEntity);
    }

    @Override
    @Transactional
    public void markEventAsConsumed(final UUID eventId, final Date consumedAt) {
        final EventConsumedEntity eventConsumedEntity = Optional.ofNullable(entityManager.find(EventConsumedEntity.class, eventId))
                .orElseGet(() -> new EventConsumedEntity(eventId));
        eventConsumedEntity.markAsConsumed();
        entityManager.persist(eventConsumedEntity);
    }

    @Override
    @Transactional
    public boolean hasConsumedEvent(final UUID eventId) {
        return Optional.ofNullable(entityManager.find(EventConsumedEntity.class, eventId))
                .map(EventConsumedEntity::consumed)
                .orElse(Boolean.FALSE);
    }

    @Override
    @Transactional
    public List<String> getConsumedEventsForEventId(final UUID eventId) {
        return entityManager.createNamedQuery("EventConsumerConsumed.getConsumedEventsForEventId",
                String.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

}