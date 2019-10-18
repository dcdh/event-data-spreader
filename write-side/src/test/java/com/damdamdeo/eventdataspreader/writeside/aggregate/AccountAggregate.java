package com.damdamdeo.eventdataspreader.writeside.aggregate;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.AccountDebitedPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.command.DebitAccountCommand;
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

    public void handle(final DebitAccountCommand debitAccountCommand) {
        apply(new AccountDebitedPayload(debitAccountCommand.owner(),
                        debitAccountCommand.price(),
                        this.balance.add(debitAccountCommand.price().negate())),
                new DefaultEventMetadata(debitAccountCommand.executedBy()));
    }

    public void on(final AccountDebitedPayload accountDebitedPayload) {
        this.owner = accountDebitedPayload.owner();
        this.balance = accountDebitedPayload.balance();
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
