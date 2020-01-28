package com.damdamdeo.eventdataspreader.queryside.interfaces;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventQualifier;
import com.damdamdeo.eventdataspreader.queryside.event.GiftAggregateGiftOfferedEventPayload;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import java.util.Objects;

@Dependent
@EventQualifier(aggregateRootType = "GiftAggregate", eventType = "GiftOffered")
public class GiftOfferedEventConsumer implements EventConsumer {

    final EntityManager entityManager;

    public GiftOfferedEventConsumer(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    public void consume(final Event event) {
        final GiftAggregateGiftOfferedEventPayload giftAggregateGiftOfferedEventPayload = (GiftAggregateGiftOfferedEventPayload) event.eventPayload();
        final Long version = event.version();
        final GiftEntity giftEntity = entityManager.find(GiftEntity.class, giftAggregateGiftOfferedEventPayload.name());
        giftEntity.onGiftOffered(giftAggregateGiftOfferedEventPayload.offeredTo(), version);
        entityManager.persist(giftEntity);
    }

}
