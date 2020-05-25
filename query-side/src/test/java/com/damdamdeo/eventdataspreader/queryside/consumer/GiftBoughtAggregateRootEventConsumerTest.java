package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumable;
import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;
import com.damdamdeo.eventdataspreader.queryside.consumer.payload.GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class GiftBoughtAggregateRootEventConsumerTest {

    @Inject
    GiftBoughtAggregateRootEventConsumer giftBoughtEventConsumer;

    @InjectMock
    GiftRepository giftRepository;

    @InjectMock
    GiftEntityProvider giftEntityProvider;

    @Test
    public void should_bought_gift() {
        // Given
        final AggregateRootEventConsumable aggregateRootEventConsumable = mock(AggregateRootEventConsumable.class);
        final GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer giftAggregateGiftBoughtEventPayload = mock(GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer.class);
        doReturn("lapinou").when(giftAggregateGiftBoughtEventPayload).name();
        doReturn(giftAggregateGiftBoughtEventPayload).when(aggregateRootEventConsumable).eventPayload();
        final AggregateRootEventId aggregateRootEventId = mock(AggregateRootEventId.class);
        doReturn(aggregateRootEventId).when(aggregateRootEventConsumable).eventId();
        final GiftEntity giftEntity = mock(GiftEntity.class);
        doReturn(giftEntity).when(giftEntityProvider).create();

        // When
        giftBoughtEventConsumer.consume(aggregateRootEventConsumable);

        // Then
        verify(giftEntity, times(1)).onGiftBought("lapinou", aggregateRootEventId);
        verify(giftRepository, times(1)).persist(giftEntity);

        verify(aggregateRootEventConsumable, times(1)).eventPayload();
        verify(aggregateRootEventConsumable, times(1)).eventId();
        verify(giftAggregateGiftBoughtEventPayload, times(1)).name();
        verify(giftEntityProvider, times(1)).create();
        verifyNoMoreInteractions(giftEntityProvider, aggregateRootEventConsumable, giftAggregateGiftBoughtEventPayload);
    }

    @Test
    public void should_apply_on_gift_aggregate_root() {
        assertEquals("GiftAggregateRoot", giftBoughtEventConsumer.aggregateRootType());
    }

    @Test
    public void should_apply_on_gift_bought_event() {
        assertEquals("GiftBought", giftBoughtEventConsumer.eventType());
    }

}
