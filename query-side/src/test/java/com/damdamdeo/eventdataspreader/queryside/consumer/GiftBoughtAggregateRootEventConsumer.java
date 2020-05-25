package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumable;
import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumer;
import com.damdamdeo.eventdataspreader.queryside.consumer.payload.GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class GiftBoughtAggregateRootEventConsumer implements AggregateRootEventConsumer {

    final GiftRepository giftRepository;
    final GiftEntityProvider giftEntityProvider;

    public GiftBoughtAggregateRootEventConsumer(final GiftRepository giftRepository,
                                                final GiftEntityProvider giftEntityProvider) {
        this.giftRepository = Objects.requireNonNull(giftRepository);
        this.giftEntityProvider = Objects.requireNonNull(giftEntityProvider);
    }

    @Override
    public void consume(final AggregateRootEventConsumable aggregateRootEventConsumable) {
        final GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer giftAggregateGiftBoughtEventPayload = (GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer) aggregateRootEventConsumable.eventPayload();
        final GiftEntity giftEntity = this.giftEntityProvider.create();
        giftEntity.onGiftBought(giftAggregateGiftBoughtEventPayload.name(), aggregateRootEventConsumable.eventId());
        giftRepository.persist(giftEntity);
    }

    @Override
    public String aggregateRootType() {
        return "GiftAggregateRoot";
    }

    @Override
    public String eventType() {
        return "GiftBought";
    }

}
