package com.damdamdeo.eventdataspreader.writeside.aggregate.payload;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRoot;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class GiftAggregateRootGiftBoughtAggregateRootEventPayloadTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(GiftAggregateRootGiftBoughtAggregateRootEventPayload.class).verify();
    }

    @Test
    public void should_apply_event_on_gift_aggregate_root() {
        // Given
        final GiftAggregateRoot giftAggregateRoot = mock(GiftAggregateRoot.class);
        final GiftAggregateRootGiftBoughtAggregateRootEventPayload giftAggregateRootGiftBoughtAggregateRootEventPayload
                = new GiftAggregateRootGiftBoughtAggregateRootEventPayload("lapinou");

        // When
        giftAggregateRootGiftBoughtAggregateRootEventPayload.apply(giftAggregateRoot);

        // Then
        verify(giftAggregateRoot, times(1)).on(giftAggregateRootGiftBoughtAggregateRootEventPayload);
        verifyNoMoreInteractions(giftAggregateRoot);
    }

}
