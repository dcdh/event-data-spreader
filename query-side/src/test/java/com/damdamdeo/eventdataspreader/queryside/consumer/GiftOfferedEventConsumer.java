package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.event.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.queryside.event.GiftAggregateGiftOfferedEventPayload;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class GiftOfferedEventConsumer implements EventConsumer {

    final GiftRepository giftRepository;

    public GiftOfferedEventConsumer(final GiftRepository giftRepository) {
        this.giftRepository = Objects.requireNonNull(giftRepository);
    }

    @Override
    public void consume(final Event event) {
        final GiftAggregateGiftOfferedEventPayload giftAggregateGiftOfferedEventPayload = (GiftAggregateGiftOfferedEventPayload) event.eventPayload();
        final Long version = event.version();
        final GiftEntity giftEntity = giftRepository.find(giftAggregateGiftOfferedEventPayload.name());
        giftEntity.onGiftOffered(giftAggregateGiftOfferedEventPayload.offeredTo(), version);
        giftRepository.persist(giftEntity);
    }

    @Override
    public String aggregateRootType() {
        return "GiftAggregate";
    }

    @Override
    public String eventType() {
        return "GiftOffered";
    }

}
