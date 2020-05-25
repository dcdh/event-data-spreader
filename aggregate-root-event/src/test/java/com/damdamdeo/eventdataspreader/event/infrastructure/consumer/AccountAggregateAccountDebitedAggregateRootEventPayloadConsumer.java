package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventPayloadConsumer;

import java.math.BigDecimal;
import java.util.Objects;

public class AccountAggregateAccountDebitedAggregateRootEventPayloadConsumer extends AggregateRootEventPayloadConsumer {

    private final String owner;
    private final BigDecimal balance;

    public AccountAggregateAccountDebitedAggregateRootEventPayloadConsumer(final String owner,
                                                                           final BigDecimal balance) {
        this.owner = Objects.requireNonNull(owner);
        this.balance = Objects.requireNonNull(balance);
    }

    public String owner() {
        return owner;
    }

    public BigDecimal balance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountAggregateAccountDebitedAggregateRootEventPayloadConsumer that = (AccountAggregateAccountDebitedAggregateRootEventPayloadConsumer) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, balance);
    }
}
