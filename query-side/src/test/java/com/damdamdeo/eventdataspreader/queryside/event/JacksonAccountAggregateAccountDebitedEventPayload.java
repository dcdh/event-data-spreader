package com.damdamdeo.eventdataspreader.queryside.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class JacksonAccountAggregateAccountDebitedEventPayload implements AccountAggregateAccountDebitedEventPayload {

    private final String owner;
    private final BigDecimal balance;

    @JsonCreator
    public JacksonAccountAggregateAccountDebitedEventPayload(@JsonProperty("owner") final String owner,
                                                             @JsonProperty("balance") final BigDecimal balance) {
        this.owner = Objects.requireNonNull(owner);
        this.balance = Objects.requireNonNull(balance);
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
        if (!(o instanceof JacksonAccountAggregateAccountDebitedEventPayload)) return false;
        JacksonAccountAggregateAccountDebitedEventPayload that = (JacksonAccountAggregateAccountDebitedEventPayload) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, balance);
    }

    @Override
    public String toString() {
        return "AccountDebitedEventPayload{" +
                "owner='" + owner + '\'' +
                ", balance=" + balance +
                '}';
    }
}
