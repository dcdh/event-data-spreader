package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumable;
import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumer;
import com.damdamdeo.eventdataspreader.queryside.consumer.payload.GiftAggregateRootGiftOfferedAggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class GiftOfferedAggregateRootEventConsumer implements AggregateRootEventConsumer {

    final GiftRepository giftRepository;

    public GiftOfferedAggregateRootEventConsumer(final GiftRepository giftRepository) {
        this.giftRepository = Objects.requireNonNull(giftRepository);
    }

    @Override
    public void consume(final AggregateRootEventConsumable aggregateRootEventConsumable) {
        final GiftAggregateRootGiftOfferedAggregateRootEventPayloadConsumer giftAggregateGiftOfferedEventPayload = (GiftAggregateRootGiftOfferedAggregateRootEventPayloadConsumer) aggregateRootEventConsumable.eventPayload();
        final GiftEntity giftEntity = giftRepository.find(giftAggregateGiftOfferedEventPayload.name());
        giftEntity.onGiftOffered(giftAggregateGiftOfferedEventPayload.offeredTo(), aggregateRootEventConsumable.eventId());
        giftRepository.persist(giftEntity);
    }

    @Override
    public String aggregateRootType() {
        return "GiftAggregateRoot";
    }

    @Override
    public String eventType() {
        return "GiftOffered";
    }

}
