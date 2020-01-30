package com.damdamdeo.eventdataspreader.writeside.command;

import com.damdamdeo.eventdataspreader.writeside.command.api.Command;

import java.util.Objects;

public final class BuyGiftCommand implements Command {

    private final String name;

    private final String executedBy;

    public BuyGiftCommand(final String name,
                          final String executedBy) {
        this.name = Objects.requireNonNull(name);
        this.executedBy = Objects.requireNonNull(executedBy);
    }

    @Override
    public String aggregateId() {
        return name;
    }

    public String name() {
        return name;
    }

    public String executedBy() {
        return executedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BuyGiftCommand)) return false;
        BuyGiftCommand that = (BuyGiftCommand) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(executedBy, that.executedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, executedBy);
    }

    @Override
    public String toString() {
        return "BuyGiftCommand{" +
                "name='" + name + '\'' +
                ", executedBy='" + executedBy + '\'' +
                '}';
    }
}
