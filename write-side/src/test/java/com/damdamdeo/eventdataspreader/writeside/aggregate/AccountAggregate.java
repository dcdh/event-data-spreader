package com.damdamdeo.eventdataspreader.writeside.aggregate;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.AccountDebited;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

import java.math.BigDecimal;
import java.util.Objects;

public class AccountAggregate extends AggregateRoot {

    private String owner;

    private BigDecimal balance = new BigDecimal("1000");

    public AccountAggregate() {}

    public AccountAggregate(final String aggregateRootId,
                            final String owner,
                            final BigDecimal balance,
                            final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.owner = Objects.requireNonNull(owner);
        this.balance = balance;
        this.version = Objects.requireNonNull(version);
    }

    public void on(final AccountDebited accountDebited) {
        this.aggregateRootId = accountDebited.owner();
        this.owner = accountDebited.owner();
        this.balance = this.balance.add(accountDebited.price().negate());
    }

    public String owner() {
        return owner;
    }

    public BigDecimal balance() {
        return balance;
    }

    @Override
    public String toString() {
        return "AccountAggregate{" +
                "owner='" + owner + '\'' +
                ", balance=" + balance +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                ", version=" + version +
                '}';
    }

}
