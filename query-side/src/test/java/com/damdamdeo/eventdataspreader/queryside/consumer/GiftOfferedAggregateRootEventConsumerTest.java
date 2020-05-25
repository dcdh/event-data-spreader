package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumable;
import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;
import com.damdamdeo.eventdataspreader.queryside.consumer.payload.GiftAggregateRootGiftOfferedAggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class GiftOfferedAggregateRootEventConsumerTest {

    @Inject
    GiftOfferedAggregateRootEventConsumer giftOfferedEventConsumer;

    @InjectMock
    GiftRepository giftRepository;

    @Test
    public void should_offer_gift() {
        // Given
        final AggregateRootEventConsumable aggregateRootEventConsumable = mock(AggregateRootEventConsumable.class);
        final GiftAggregateRootGiftOfferedAggregateRootEventPayloadConsumer giftAggregateGiftOfferedEventPayload = mock(GiftAggregateRootGiftOfferedAggregateRootEventPayloadConsumer.class);
        doReturn("lapinou").when(giftAggregateGiftOfferedEventPayload).name();
        doReturn("damdamdeo").when(giftAggregateGiftOfferedEventPayload).offeredTo();
        doReturn(giftAggregateGiftOfferedEventPayload).when(aggregateRootEventConsumable).eventPayload();
        final AggregateRootEventId aggregateRootEventId = mock(AggregateRootEventId.class);
        doReturn(aggregateRootEventId).when(aggregateRootEventConsumable).eventId();
        final GiftEntity giftEntity = mock(GiftEntity.class);
        doReturn(giftEntity).when(giftRepository).find("lapinou");

        // When
        giftOfferedEventConsumer.consume(aggregateRootEventConsumable);

        // Then
        verify(giftEntity, times(1)).onGiftOffered("damdamdeo", aggregateRootEventId);
        verify(giftRepository, times(1)).persist(giftEntity);
        verify(giftRepository, times(1)).find(any());
        verify(aggregateRootEventConsumable, times(1)).eventPayload();
        verify(aggregateRootEventConsumable, times(1)).eventId();
        verify(giftAggregateGiftOfferedEventPayload, times(1)).name();
        verify(giftAggregateGiftOfferedEventPayload, times(1)).offeredTo();
        verifyNoMoreInteractions(giftRepository, aggregateRootEventConsumable, giftAggregateGiftOfferedEventPayload);
    }

    @Test
    public void should_apply_on_gift_aggregate_root() {
        assertEquals("GiftAggregateRoot", giftOfferedEventConsumer.aggregateRootType());
    }

    @Test
    public void should_apply_on_gift_offered_event() {
        assertEquals("GiftOffered", giftOfferedEventConsumer.eventType());
    }

}
