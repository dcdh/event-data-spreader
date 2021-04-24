package com.damdamdeo.eventsourced.mutable.eventsourcing.command;

import com.damdamdeo.eventsourced.mutable.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.eventsourcing.lock.LockingMechanism;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class CommandHandler<T extends AggregateRoot, C extends Command> {

    private static final String ONE_TIME_COMMAND_EXECUTION = "one-time-command-execution";

    private final LockingMechanism lockingMechanism;

    public CommandHandler(final LockingMechanism lockingMechanism) {
        this.lockingMechanism = Objects.requireNonNull(lockingMechanism);
    }

    public final T lockedExecution(final C command) throws Throwable {
        final List<String> locksNames;
        switch (command.commandLockingType()) {
            case GLOBAL:
                locksNames = Arrays.asList(command.aggregateRootId(), ONE_TIME_COMMAND_EXECUTION);
                break;
            case AGGREGATE_ONLY:
                locksNames = Arrays.asList(Objects.requireNonNull(command.aggregateRootId(), "aggregateRootId must be defined when locking on aggregate root !"));
                break;
            default:
                throw new IllegalStateException("Should not be here !");
        }
        try {
            lockingMechanism.lockUntilReleased(locksNames);
            return execute(command);
        } finally {
            lockingMechanism.unlock(locksNames);
        }
    }

    protected abstract T execute(final C command) throws Throwable;

}
