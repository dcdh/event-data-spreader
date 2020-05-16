package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.event.api.Event;
import com.damdamdeo.eventdataspreader.queryside.event.GiftAggregateGiftOfferedEventPayload;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class GiftOfferedEventConsumerTest {

    @Inject
    GiftOfferedEventConsumer giftOfferedEventConsumer;

    @InjectMock
    GiftRepository giftRepository;

    @Test
    public void should_offer_gift() {
        // Given
        final Event event = mock(Event.class);
        final GiftAggregateGiftOfferedEventPayload giftAggregateGiftOfferedEventPayload = mock(GiftAggregateGiftOfferedEventPayload.class);
        doReturn("lapinou").when(giftAggregateGiftOfferedEventPayload).name();
        doReturn("damdamdeo").when(giftAggregateGiftOfferedEventPayload).offeredTo();
        doReturn(giftAggregateGiftOfferedEventPayload).when(event).eventPayload();
        doReturn(1l).when(event).version();
        final GiftEntity giftEntity = mock(GiftEntity.class);
        doReturn(giftEntity).when(giftRepository).find("lapinou");

        // When
        giftOfferedEventConsumer.consume(event);

        // Then
        verify(giftEntity, times(1)).onGiftOffered("damdamdeo", 1l);
        verify(giftRepository, times(1)).persist(giftEntity);
        verify(giftRepository, times(1)).find(any());
        verify(event, times(1)).eventPayload();
        verify(event, times(1)).version();
        verify(giftAggregateGiftOfferedEventPayload, times(1)).name();
        verify(giftAggregateGiftOfferedEventPayload, times(1)).offeredTo();
        verifyNoMoreInteractions(giftRepository, event, giftAggregateGiftOfferedEventPayload);
    }

    @Test
    public void should_apply_on_gift_aggregate_root() {
        assertEquals("GiftAggregate", giftOfferedEventConsumer.aggregateRootType());
    }

    @Test
    public void should_apply_on_gift_offered_event() {
        assertEquals("GiftOffered", giftOfferedEventConsumer.eventType());
    }

}
