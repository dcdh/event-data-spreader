package com.damdamdeo.eventdataspreader.writeside.command.api;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ApplicationScoped
public class DefaultCommandHandler {

    private ExecutorService exactlyOnceCommandExecutor;
    private List<ExecutorService> threadPools;
    private Set<String> handledAggregateRootIdsInExactlyOnce;

    @PostConstruct
    public void init() {
        this.exactlyOnceCommandExecutor = Executors.newSingleThreadExecutor();
        final List<ExecutorService> list = IntStream
                .range(0, 20)
                .mapToObj(i -> Executors.newSingleThreadExecutor())
                .collect(Collectors.toList());
        this.threadPools = new CopyOnWriteArrayList<>(list);
        this.handledAggregateRootIdsInExactlyOnce = Collections.synchronizedSet(new HashSet<>());
    }

    @PreDestroy
    public void destroy() {
        this.exactlyOnceCommandExecutor.shutdown();
        this.threadPools.stream().forEach(ExecutorService::shutdown);
    }

    public AggregateRoot handle(final Command command) throws Throwable {
        final ExecutorService executorServiceToExecuteCommand;
        if (command.exactlyOnceCommandExecution()) {
            executorServiceToExecuteCommand = this.exactlyOnceCommandExecutor;
            this.handledAggregateRootIdsInExactlyOnce.add(command.aggregateId());
        } else if (this.handledAggregateRootIdsInExactlyOnce.contains(command.aggregateId())) {
            executorServiceToExecuteCommand = this.exactlyOnceCommandExecutor;
        } else if (command.aggregateId() == null) {
            executorServiceToExecuteCommand = this.threadPools.get(0);
        } else {
            final int threadIdx = Math.abs(command.aggregateId().hashCode()) % threadPools.size();
            executorServiceToExecuteCommand = threadPools.get(threadIdx);
        }
        try {
            return executorServiceToExecuteCommand.submit(() -> {
                final AggregateRoot aggregateRoot = executeCommand(command);
                this.handledAggregateRootIdsInExactlyOnce.remove(aggregateRoot.aggregateRootId());
                return aggregateRoot;
            }).get();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        } catch (final ExecutionException e) {
            throw e.getCause();
        }
    }

    private AggregateRoot executeCommand(final Command command) {
        final CommandHandler commandHandler = CDI.current()
                .select(CommandHandler.class)
                // FCK !!!
                .get();
        return commandHandler.handle(command);
    }

}
