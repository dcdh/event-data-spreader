package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.event.api.Event;
import com.damdamdeo.eventdataspreader.queryside.event.GiftAggregateGiftBoughtEventPayload;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class GiftBoughtEventConsumerTest {

    @Inject
    GiftBoughtEventConsumer giftBoughtEventConsumer;

    @InjectMock
    GiftRepository giftRepository;

    @InjectMock
    GiftEntityProvider giftEntityProvider;

    @Test
    public void should_bought_gift() {
        // Given
        final Event event = mock(Event.class);
        final GiftAggregateGiftBoughtEventPayload giftAggregateGiftBoughtEventPayload = mock(GiftAggregateGiftBoughtEventPayload.class);
        doReturn("lapinou").when(giftAggregateGiftBoughtEventPayload).name();
        doReturn(giftAggregateGiftBoughtEventPayload).when(event).eventPayload();
        doReturn(0l).when(event).version();
        final GiftEntity giftEntity = mock(GiftEntity.class);
        doReturn(giftEntity).when(giftEntityProvider).create();

        // When
        giftBoughtEventConsumer.consume(event);

        // Then
        verify(giftEntity, times(1)).onGiftBought("lapinou", 0l);
        verify(giftRepository, times(1)).persist(giftEntity);

        verify(event, times(1)).eventPayload();
        verify(event, times(1)).version();
        verify(giftAggregateGiftBoughtEventPayload, times(1)).name();
        verify(giftEntityProvider, times(1)).create();
        verifyNoMoreInteractions(giftEntityProvider, event, giftAggregateGiftBoughtEventPayload);
    }

    @Test
    public void should_apply_on_gift_aggregate_root() {
        assertEquals("GiftAggregate", giftBoughtEventConsumer.aggregateRootType());
    }

    @Test
    public void should_apply_on_gift_bought_event() {
        assertEquals("GiftBought", giftBoughtEventConsumer.eventType());
    }

}
