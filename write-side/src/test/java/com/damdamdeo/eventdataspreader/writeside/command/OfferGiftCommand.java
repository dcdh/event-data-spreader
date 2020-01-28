package com.damdamdeo.eventdataspreader.writeside.command;

import com.damdamdeo.eventdataspreader.writeside.command.api.Command;

import java.util.Objects;

public final class OfferGiftCommand implements Command {

    private final String name;

    private final String offeredTo;

    private final String executedBy;

    public OfferGiftCommand(final String name,
                            final String offeredTo,
                            final String executedBy) {
        this.name = Objects.requireNonNull(name);
        this.offeredTo = Objects.requireNonNull(offeredTo);
        this.executedBy = Objects.requireNonNull(executedBy);
    }

    public String name() {
        return name;
    }

    public String offeredTo() {
        return offeredTo;
    }

    public String executedBy() {
        return executedBy;
    }

    @Override
    public String aggregateId() {
        return name;
    }

    @Override
    public boolean exactlyOnceCommandExecution() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OfferGiftCommand)) return false;
        OfferGiftCommand that = (OfferGiftCommand) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(offeredTo, that.offeredTo) &&
                Objects.equals(executedBy, that.executedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, offeredTo, executedBy);
    }

    @Override
    public String toString() {
        return "OfferGiftCommand{" +
                "name='" + name + '\'' +
                ", offeredTo='" + offeredTo + '\'' +
                ", executedBy='" + executedBy + '\'' +
                '}';
    }
}
