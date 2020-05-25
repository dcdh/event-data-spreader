package com.damdamdeo.eventdataspreader.writeside.aggregate.payload;

import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregateRoot;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

public class AccountAggregateRootAccountDebitedAggregateRootEventPayloadTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(AccountAggregateRootAccountDebitedAggregateRootEventPayload.class).verify();
    }

    @Test
    public void should_apply_event_on_account_aggregate_root() {
        // Given
        final AccountAggregateRoot accountAggregateRoot = mock(AccountAggregateRoot.class);
        final AccountAggregateRootAccountDebitedAggregateRootEventPayload accountAggregateRootAccountDebitedAggregateRootEventPayload
                = new AccountAggregateRootAccountDebitedAggregateRootEventPayload("owner", BigDecimal.ONE, BigDecimal.TEN);

        // When
        accountAggregateRootAccountDebitedAggregateRootEventPayload.apply(accountAggregateRoot);

        // Then
        verify(accountAggregateRoot, times(1)).on(accountAggregateRootAccountDebitedAggregateRootEventPayload);
        verifyNoMoreInteractions(accountAggregateRoot);
    }

}
