package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command;

import com.damdamdeo.eventsourced.mutable.eventsourcing.command.Command;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.InvocationContext;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@ApplicationScoped
public class CommandExecutor {

    private static final String ONE_TIME_COMMAND_EXECUTION = "one-time-command-execution";

    private final HazelcastInstance hazelcastClient;

    public CommandExecutor(final HazelcastInstance hazelcastClient) {
        this.hazelcastClient = Objects.requireNonNull(hazelcastClient);
    }

    public Object execute(final InvocationContext context) throws Throwable {
        final List<String> locksNames;
        final Command command = (Command) context.getParameters()[0];
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
            executeOnEachLock(locksNames, FencedLock::lock);
            return context.proceed();
        } catch (final ExecutionException executionException) {
            throw executionException.getCause();
        } finally {
            executeOnEachLock(locksNames, FencedLock::unlock);
        }
    }

    private void executeOnEachLock(final List<String> locksNames, final Consumer<FencedLock> fencedLockConsumer) {
        locksNames.stream()
                .filter(lockName -> lockName != null)
                .map(lockName -> hazelcastClient.getCPSubsystem().getLock(lockName))
                .forEach(fencedLockConsumer);
    }

}
