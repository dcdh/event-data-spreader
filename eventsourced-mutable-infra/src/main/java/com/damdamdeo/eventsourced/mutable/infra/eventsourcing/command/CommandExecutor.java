package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class CommandExecutor {

    final ExecutorService executorService;

    public CommandExecutor() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public <T> T execute(Callable<T> callable) throws Throwable {
        try {
            return this.executorService.submit(callable).get();
        } catch (final ExecutionException executionException) {
            throw executionException.getCause();
        }
    }

}
