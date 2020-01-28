package com.damdamdeo.eventdataspreader.writeside.aggregate;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.AccountAggregateAccountDebitedEventPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.command.DebitAccountCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class AccountAggregate extends AggregateRoot {

    private String owner;

    private BigDecimal balance = new BigDecimal("1000");

    public AccountAggregate() {}

    @JsonCreator
    public AccountAggregate(@JsonProperty("aggregateRootId") final String aggregateRootId,
                            @JsonProperty("owner") final String owner,
                            @JsonProperty("balance") final BigDecimal balance,
                            @JsonProperty("version") final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.owner = Objects.requireNonNull(owner);
        this.balance = balance;
        this.version = Objects.requireNonNull(version);
    }

    public void handle(final DebitAccountCommand debitAccountCommand) {
        apply(new AccountAggregateAccountDebitedEventPayload(debitAccountCommand.owner(),
                        debitAccountCommand.price(),
                        this.balance.add(debitAccountCommand.price().negate())),
                new DefaultEventMetadata(debitAccountCommand.executedBy()));
    }

    public void on(final AccountAggregateAccountDebitedEventPayload accountDebitedPayload) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountAggregate)) return false;
        AccountAggregate that = (AccountAggregate) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, balance);
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
