package com.damdamdeo.eventdataspreader.writeside.command;

import com.damdamdeo.eventdataspreader.writeside.command.api.Command;

import java.util.Objects;

public class BuyGiftCommand implements Command {

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
    public boolean exactlyOnceCommandExecution() {
        return false;
    }

}
