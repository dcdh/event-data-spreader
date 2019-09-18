package com.damdamdeo.eventdataspreader.writeside.eventsourcing.command;

import com.damdamdeo.eventdataspreader.writeside.command.api.Command;

import java.util.Objects;

public class OfferGiftCommand implements Command {

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

}
