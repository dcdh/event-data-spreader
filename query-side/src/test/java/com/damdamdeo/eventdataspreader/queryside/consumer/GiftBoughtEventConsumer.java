package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.event.api.Event;
import com.damdamdeo.eventdataspreader.event.api.consumer.EventConsumer;
import com.damdamdeo.eventdataspreader.queryside.event.GiftAggregateGiftBoughtEventPayload;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class GiftBoughtEventConsumer implements EventConsumer {

    final GiftRepository giftRepository;
    final GiftEntityProvider giftEntityProvider;

    public GiftBoughtEventConsumer(final GiftRepository giftRepository,
                                   final GiftEntityProvider giftEntityProvider) {
        this.giftRepository = Objects.requireNonNull(giftRepository);
        this.giftEntityProvider = Objects.requireNonNull(giftEntityProvider);
    }

    @Override
    public void consume(final Event event) {
        final GiftAggregateGiftBoughtEventPayload giftAggregateGiftBoughtEventPayload = (GiftAggregateGiftBoughtEventPayload) event.eventPayload();
        final Long version = event.version();
        final GiftEntity giftEntity = this.giftEntityProvider.create();
        giftEntity.onGiftBought(giftAggregateGiftBoughtEventPayload.name(), version);
        giftRepository.persist(giftEntity);
    }

    @Override
    public String aggregateRootType() {
        return "GiftAggregate";
    }

    @Override
    public String eventType() {
        return "GiftBought";
    }

}
