package com.damdamdeo.eventdataspreader.writeside.command;

import com.damdamdeo.eventdataspreader.writeside.command.api.Command;

import java.math.BigDecimal;
import java.util.Objects;

public class DebitAccountCommand implements Command {

    private final String owner;

    private final BigDecimal price;

    private final String executedBy;

    public DebitAccountCommand(final String owner, final BigDecimal price, final String executedBy) {
        this.owner = Objects.requireNonNull(owner);
        this.price = Objects.requireNonNull(price);
        this.executedBy = Objects.requireNonNull(executedBy);
    }

    @Override
    public String aggregateId() {
        return owner;
    }

    public String owner() {
        return owner;
    }

    public BigDecimal price() {
        return price;
    }

    public String executedBy() {
        return executedBy;
    }

    @Override
    public boolean exactlyOnceCommandExecution() {
        return false;
    }

}
