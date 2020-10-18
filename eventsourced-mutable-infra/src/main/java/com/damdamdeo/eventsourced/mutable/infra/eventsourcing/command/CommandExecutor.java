package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class CommandExecutor {

    private static final String ONE_TIME_COMMAND_EXECUTION = "one-time-command-execution";

    final HazelcastInstance hazelcastClient;

    public CommandExecutor(final HazelcastInstance hazelcastClient) {
        this.hazelcastClient = Objects.requireNonNull(hazelcastClient);
    }

    public <T> T execute(Callable<T> callable) throws Throwable {
        final FencedLock fencedLock = hazelcastClient
                .getCPSubsystem()
                .getLock(ONE_TIME_COMMAND_EXECUTION);
        try {
            fencedLock.lock();
            return callable.call();
        } catch (final ExecutionException executionException) {
            throw executionException.getCause();
        } finally {
            fencedLock.unlock();
        }
    }

}
