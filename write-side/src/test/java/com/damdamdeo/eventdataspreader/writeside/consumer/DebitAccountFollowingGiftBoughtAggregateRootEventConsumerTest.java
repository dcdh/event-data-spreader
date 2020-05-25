package com.damdamdeo.eventdataspreader.writeside.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumable;
import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.command.DebitAccountCommand;
import com.damdamdeo.eventdataspreader.writeside.consumer.metadata.UserAggregateRootEventMetadataConsumer;
import com.damdamdeo.eventdataspreader.writeside.consumer.payload.GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class DebitAccountFollowingGiftBoughtAggregateRootEventConsumerTest {

    @Inject
    DebitAccountFollowingGiftBoughtAggregateRootEventConsumer debitAccountFollowingGiftBoughtAggregateRootEventConsumer;

    @InjectMock
    AggregateRootRepository aggregateRootRepository;

    @InjectMock
    AccountAggregateRootProvider accountAggregateRootProvider;

    @BeforeEach
    @AfterEach
    public void flush() {
        reset(aggregateRootRepository, accountAggregateRootProvider);
    }

    @Test
    public void should_debit_account_following_gift_bought_event() {
        // Given
        final AggregateRootEventConsumable aggregateRootEventConsumable = mock(AggregateRootEventConsumable.class);
        final GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer giftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer = (GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer) aggregateRootEventConsumable.eventPayload();

        final UserAggregateRootEventMetadataConsumer eventMetadata = mock(UserAggregateRootEventMetadataConsumer.class);
        doReturn(eventMetadata).when(aggregateRootEventConsumable).eventMetaData();
        doReturn("executedBy").when(eventMetadata).executedBy();
        final AccountAggregateRoot accountAggregateRoot = mock(AccountAggregateRoot.class);
        doReturn(accountAggregateRoot).when(accountAggregateRootProvider).create();

        // When
        debitAccountFollowingGiftBoughtAggregateRootEventConsumer.consume(aggregateRootEventConsumable);

        // Then
        verify(accountAggregateRoot, times(1)).handle(new DebitAccountCommand("executedBy",
                new BigDecimal("100"), "executedBy"));
        verify(aggregateRootRepository, times(1)).save(accountAggregateRoot);

        verify(aggregateRootEventConsumable, times(1)).eventMetaData();
        verify(aggregateRootEventConsumable, times(2)).eventPayload();
        verify(aggregateRootEventConsumable, times(1)).eventId();
        verify(eventMetadata, times(2)).executedBy();
        verify(accountAggregateRootProvider, times(1)).create();
        verifyNoMoreInteractions(aggregateRootRepository, aggregateRootEventConsumable, eventMetadata, accountAggregateRootProvider);
    }

    @Test
    public void should_apply_on_gift_aggregate_root() {
        assertEquals("GiftAggregateRoot", debitAccountFollowingGiftBoughtAggregateRootEventConsumer.aggregateRootType());
    }

    @Test
    public void should_apply_on_gift_bought_event() {
        assertEquals("GiftAggregateRootGiftBoughtAggregateRootEventPayload", debitAccountFollowingGiftBoughtAggregateRootEventConsumer.eventType());
    }

}
