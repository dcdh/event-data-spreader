package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.event.api.Event;
import com.damdamdeo.eventdataspreader.queryside.event.AccountAggregateAccountDebitedEventPayload;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.AccountEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class AccountDebitedEventConsumerTest {

    @Inject
    AccountDebitedEventConsumer accountDebitedEventConsumer;

    @InjectMock
    AccountRepository accountRepository;

    @InjectMock
    AccountEntityProvider accountEntityProvider;

    @Test
    public void should_create_account_debited() {
        // Given
        final Event event = mock(Event.class);
        final AccountAggregateAccountDebitedEventPayload accountAggregateAccountDebitedEventPayload = mock(AccountAggregateAccountDebitedEventPayload.class);
        doReturn("damdamdeo").when(accountAggregateAccountDebitedEventPayload).owner();
        doReturn(BigDecimal.ONE).when(accountAggregateAccountDebitedEventPayload).balance();
        doReturn(accountAggregateAccountDebitedEventPayload).when(event).eventPayload();
        doReturn(0l).when(event).version();
        final AccountEntity accountEntity = mock(AccountEntity.class);
        doReturn(accountEntity).when(accountEntityProvider).create();

        // When
        accountDebitedEventConsumer.consume(event);

        // Then
        verify(accountEntity, times(1)).onAccountDebited("damdamdeo", BigDecimal.ONE, 0l);
        verify(accountRepository, times(1)).persist(accountEntity);

        verify(event, times(1)).eventPayload();
        verify(event, times(1)).version();
        verify(accountAggregateAccountDebitedEventPayload, times(1)).owner();
        verify(accountAggregateAccountDebitedEventPayload, times(1)).balance();
        verify(accountEntityProvider, times(1)).create();
        verifyNoMoreInteractions(accountEntityProvider, event, accountAggregateAccountDebitedEventPayload);
    }

    @Test
    public void should_apply_on_account_aggregate_root() {
        assertEquals("AccountAggregate", accountDebitedEventConsumer.aggregateRootType());
    }

    @Test
    public void should_apply_on_account_debited_event() {
        assertEquals("AccountDebited", accountDebitedEventConsumer.eventType());
    }

}
