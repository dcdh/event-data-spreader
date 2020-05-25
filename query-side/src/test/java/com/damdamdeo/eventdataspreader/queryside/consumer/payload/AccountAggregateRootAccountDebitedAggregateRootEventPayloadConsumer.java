package com.damdamdeo.eventdataspreader.queryside.consumer.payload;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventPayloadConsumer;

import java.math.BigDecimal;
import java.util.Objects;

public final class AccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumer extends AggregateRootEventPayloadConsumer {

    private final String owner;

    private final BigDecimal balance;

    public AccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumer(final String owner, final BigDecimal balance) {
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
        AccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumer that = (AccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumer) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, balance);
    }
}
