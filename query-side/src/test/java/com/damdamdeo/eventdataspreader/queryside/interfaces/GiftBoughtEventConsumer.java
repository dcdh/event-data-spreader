package com.damdamdeo.eventdataspreader.queryside.interfaces;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventQualifier;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import java.util.Objects;

@ApplicationScoped
@EventQualifier(aggregateRootType = "GiftAggregate", eventType = "GiftBought")
public class GiftBoughtEventConsumer implements EventConsumer {

    final EntityManager entityManager;

    public GiftBoughtEventConsumer(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    public void consume(final Event event) {
        final String name = event.payload().getString("name");
        final Long version = event.version();
        final GiftEntity giftEntity = new GiftEntity();
        giftEntity.onGiftBought(name, version);
        entityManager.persist(giftEntity);
    }

}
