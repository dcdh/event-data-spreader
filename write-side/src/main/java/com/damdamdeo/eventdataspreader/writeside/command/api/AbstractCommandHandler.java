package com.damdamdeo.eventdataspreader.writeside.command.api;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

import java.util.Objects;

public abstract class AbstractCommandHandler<T extends AggregateRoot, C extends Command> implements CommandHandler<T, C> {

    final CommandExecutor commandExecutor;

    public AbstractCommandHandler(CommandExecutor commandExecutor) {
        this.commandExecutor = Objects.requireNonNull(commandExecutor);
    }

    protected abstract T handle(C command);

    public T executeCommand(final C command) throws Exception {
        return this.commandExecutor.execute(() -> handle(command));
    }

}
