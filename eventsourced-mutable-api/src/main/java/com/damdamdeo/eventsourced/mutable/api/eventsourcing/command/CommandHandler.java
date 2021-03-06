package com.damdamdeo.eventsourced.mutable.api.eventsourcing.command;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;

public interface CommandHandler<T extends AggregateRoot, C extends Command> {

    T execute(final C command) throws Throwable;

}
