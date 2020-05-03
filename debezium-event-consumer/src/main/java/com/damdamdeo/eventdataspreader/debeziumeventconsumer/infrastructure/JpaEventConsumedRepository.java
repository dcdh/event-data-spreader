package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumedRepository;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.KafkaSource;
import com.damdamdeo.eventdataspreader.event.api.EventId;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.*;

@Dependent
public class JpaEventConsumedRepository implements EventConsumedRepository {

    final EntityManager entityManager;

    public JpaEventConsumedRepository(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    @Transactional
    public void addEventConsumerConsumed(final EventId eventId, final Class consumerClass, final KafkaSource kafkaSource, final String gitCommitId) {
        EventConsumedEntity eventConsumedEntity;
        try {
            eventConsumedEntity = entityManager.createNamedQuery("Events.findByEventId", EventConsumedEntity.class)
                    .setParameter("aggregateRootId", eventId.aggregateRootId())
                    .setParameter("aggregateRootType", eventId.aggregateRootType())
                    .setParameter("version", eventId.version())
                    .getSingleResult();
        } catch (final NoResultException e) {
            eventConsumedEntity = new EventConsumedEntity(eventId, kafkaSource);
        }
        eventConsumedEntity.addNewEventConsumerConsumed(consumerClass, new Date(), gitCommitId);
        entityManager.persist(eventConsumedEntity);
    }

    @Override
    @Transactional
    public void markEventAsConsumed(final EventId eventId, final Date consumedAt, final KafkaSource kafkaSource) {
        final EventConsumedEntity eventConsumedEntity = Optional.ofNullable(entityManager.find(EventConsumedEntity.class, new EventConsumedId(eventId)))
                .orElseGet(() -> new EventConsumedEntity(eventId, kafkaSource));
        eventConsumedEntity.markAsConsumed();
        entityManager.persist(eventConsumedEntity);
    }

    @Override
    @Transactional
    public boolean hasConsumedEvent(final EventId eventId) {
        return Optional.ofNullable(entityManager.find(EventConsumedEntity.class, new EventConsumedId(eventId)))
                .map(EventConsumedEntity::consumed)
                .orElse(Boolean.FALSE);
    }

    @Override
    @Transactional
    public List<String> getConsumedEventsForEventId(final EventId eventId) {
        return entityManager.createNamedQuery("EventConsumerConsumed.getConsumedEventsForEventId",
                String.class)
                .setParameter("aggregateRootId", eventId.aggregateRootId())
                .setParameter("aggregateRootType", eventId.aggregateRootType())
                .setParameter("version", eventId.version())
                .getResultList();
    }

}
