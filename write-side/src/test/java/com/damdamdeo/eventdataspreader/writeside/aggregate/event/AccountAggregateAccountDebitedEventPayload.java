package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregate;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class AccountAggregateAccountDebitedEventPayload extends AggregateRootEventPayload<AccountAggregate> {

    private final String owner;
    private final BigDecimal price;
    private final BigDecimal balance;

    @JsonCreator
    public AccountAggregateAccountDebitedEventPayload(@JsonProperty("owner") final String owner,
                                                      @JsonProperty("price") final BigDecimal price,
                                                      @JsonProperty("balance") final BigDecimal balance) {
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
    public String eventName() {
        return "AccountDebited";
    }

    @Override
    public String aggregateRootId() {
        return owner;
    }

    @Override
    public String aggregateRootType() {
        return "AccountAggregate";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountAggregateAccountDebitedEventPayload)) return false;
        AccountAggregateAccountDebitedEventPayload that = (AccountAggregateAccountDebitedEventPayload) o;
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
        return "AccountDebitedPayload{" +
                "owner='" + owner + '\'' +
                ", price=" + price +
                ", balance=" + balance +
                '}';
    }
}
