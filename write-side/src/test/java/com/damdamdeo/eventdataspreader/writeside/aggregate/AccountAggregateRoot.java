package com.damdamdeo.eventdataspreader.writeside.aggregate;

import com.damdamdeo.eventdataspreader.writeside.aggregate.metadata.UserAggregateRootEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.aggregate.payload.AccountAggregateRootAccountDebitedAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.api.Account;
import com.damdamdeo.eventdataspreader.writeside.command.DebitAccountCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

import java.math.BigDecimal;
import java.util.Objects;

public final class AccountAggregateRoot extends AggregateRoot implements Account {

    private String owner;

    private BigDecimal balance = new BigDecimal("1000");

    public AccountAggregateRoot() {}

    public AccountAggregateRoot(final String aggregateRootId,
                                final String owner,
                                final BigDecimal balance,
                                final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.owner = Objects.requireNonNull(owner);
        this.balance = balance;
        this.version = Objects.requireNonNull(version);
    }

    public void handle(final DebitAccountCommand debitAccountCommand) {
        apply(new AccountAggregateRootAccountDebitedAggregateRootEventPayload(debitAccountCommand.owner(),
                        debitAccountCommand.price(),
                        this.balance.add(debitAccountCommand.price().negate())),
                new UserAggregateRootEventMetadata(debitAccountCommand.executedBy()));
    }

    public void on(final AccountAggregateRootAccountDebitedAggregateRootEventPayload accountDebitedPayload) {
        this.owner = accountDebitedPayload.owner();
        this.balance = accountDebitedPayload.balance();
    }

    @Override
    public String owner() {
        return owner;
    }

    @Override
    public BigDecimal balance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountAggregateRoot)) return false;
        AccountAggregateRoot that = (AccountAggregateRoot) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, balance);
    }

    @Override
    public String toString() {
        return "AccountAggregateRoot{" +
                "owner='" + owner + '\'' +
                ", balance=" + balance +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                ", version=" + version +
                '}';
    }
}
