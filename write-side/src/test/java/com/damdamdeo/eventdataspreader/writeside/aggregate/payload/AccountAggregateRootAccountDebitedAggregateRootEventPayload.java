package com.damdamdeo.eventdataspreader.writeside.aggregate.payload;

import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;

import java.math.BigDecimal;
import java.util.Objects;

public final class AccountAggregateRootAccountDebitedAggregateRootEventPayload extends AggregateRootEventPayload<AccountAggregateRoot> {

    private final String owner;

    private final BigDecimal price;

    private final BigDecimal balance;

    public AccountAggregateRootAccountDebitedAggregateRootEventPayload(final String owner, final BigDecimal price, final BigDecimal balance) {
        this.owner = Objects.requireNonNull(owner);
        this.price = Objects.requireNonNull(price);
        this.balance = Objects.requireNonNull(balance);
    }

    public String owner() {
        return owner;
    }

    public BigDecimal price() {
        return price;
    }

    public BigDecimal balance() {
        return balance;
    }

    @Override
    public void apply(final AccountAggregateRoot accountAggregateRoot) {
        accountAggregateRoot.on(this);
    }

    @Override
    public String eventPayloadName() {
        return "AccountAggregateRootAccountDebitedAggregateRootEventPayload";
    }

    @Override
    public String aggregateRootId() {
        return owner;
    }

    @Override
    public String aggregateRootType() {
        return "AccountAggregateRoot";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountAggregateRootAccountDebitedAggregateRootEventPayload that = (AccountAggregateRootAccountDebitedAggregateRootEventPayload) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(price, that.price) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, price, balance);
    }
}
