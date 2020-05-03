package com.damdamdeo.eventdataspreader.writeside.command.api;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class CommandExecutor {

    final ExecutorService executorService;

    public CommandExecutor() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public <T> T execute(Callable<T> callable) throws Exception {
        return this.executorService.submit(callable).get();
    }

}
