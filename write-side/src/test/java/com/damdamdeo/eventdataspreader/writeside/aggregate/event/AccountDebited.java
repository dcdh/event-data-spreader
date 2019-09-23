package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregate;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayload;

import java.math.BigDecimal;
import java.util.Objects;

public final class AccountDebited extends EventPayload<AccountAggregate> {

    private final String owner;
    private final BigDecimal price;
    private final BigDecimal balance;

    public AccountDebited(final String owner, final BigDecimal price, final BigDecimal balance) {
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
    protected void apply(final AccountAggregate accountAggregate) {
        accountAggregate.on(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountDebited)) return false;
        AccountDebited that = (AccountDebited) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(price, that.price) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, price, balance);
    }

    @Override
    public String toString() {
        return "AccountDebited{" +
                "owner='" + owner + '\'' +
                ", price=" + price +
                ", balance=" + balance +
                '}';
    }
}
