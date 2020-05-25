package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumable;
import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;
import com.damdamdeo.eventdataspreader.queryside.consumer.payload.AccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.AccountEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class AccountDebitedAggregateRootEventConsumerTest {

    @Inject
    AccountDebitedAggregateRootEventConsumer accountDebitedAggregateRootEventConsumer;

    @InjectMock
    AccountRepository accountRepository;

    @InjectMock
    AccountEntityProvider accountEntityProvider;

    @Test
    public void should_create_account_debited() {
        // Given
        final AggregateRootEventConsumable aggregateRootEventConsumable = mock(AggregateRootEventConsumable.class);
        final AccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumer accountAggregateAccountDebitedEventPayload = mock(AccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumer.class);
        doReturn("damdamdeo").when(accountAggregateAccountDebitedEventPayload).owner();
        doReturn(BigDecimal.ONE).when(accountAggregateAccountDebitedEventPayload).balance();
        doReturn(accountAggregateAccountDebitedEventPayload).when(aggregateRootEventConsumable).eventPayload();
        final AggregateRootEventId aggregateRootEventId = mock(AggregateRootEventId.class);
        doReturn(aggregateRootEventId).when(aggregateRootEventConsumable).eventId();
        final AccountEntity accountEntity = mock(AccountEntity.class);
        doReturn(accountEntity).when(accountEntityProvider).create();

        // When
        accountDebitedAggregateRootEventConsumer.consume(aggregateRootEventConsumable);

        // Then
        verify(accountEntity, times(1)).onAccountDebited("damdamdeo", BigDecimal.ONE, aggregateRootEventId);
        verify(accountRepository, times(1)).persist(accountEntity);

        verify(aggregateRootEventConsumable, times(1)).eventPayload();
        verify(aggregateRootEventConsumable, times(1)).eventId();
        verify(accountAggregateAccountDebitedEventPayload, times(1)).owner();
        verify(accountAggregateAccountDebitedEventPayload, times(1)).balance();
        verify(accountEntityProvider, times(1)).create();
        verifyNoMoreInteractions(accountEntityProvider, aggregateRootEventConsumable, accountAggregateAccountDebitedEventPayload);
    }

    @Test
    public void should_apply_on_account_aggregate_root() {
        assertEquals("AccountAggregateRoot", accountDebitedAggregateRootEventConsumer.aggregateRootType());
    }

    @Test
    public void should_apply_on_account_debited_event() {
        assertEquals("AccountDebited", accountDebitedAggregateRootEventConsumer.eventType());
    }

}
