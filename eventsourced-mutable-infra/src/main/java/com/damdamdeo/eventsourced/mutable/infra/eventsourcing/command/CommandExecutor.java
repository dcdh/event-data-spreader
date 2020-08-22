package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

@ApplicationScoped
public class CommandExecutor {

    final ReentrantLock lock;

    public CommandExecutor() {
        this.lock = new ReentrantLock();
    }

    public <T> T execute(Callable<T> callable) throws Throwable {
        try {
            lock.lock();
            return callable.call();
        } catch (final ExecutionException executionException) {
            throw executionException.getCause();
        } finally {
            lock.unlock();
        }
    }

}
