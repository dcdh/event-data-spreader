package com.damdamdeo.eventdataspreader.writeside.command.api;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

public interface CommandHandler<T extends AggregateRoot, C extends Command> {

    T executeCommand(final C command) throws Throwable;

}
