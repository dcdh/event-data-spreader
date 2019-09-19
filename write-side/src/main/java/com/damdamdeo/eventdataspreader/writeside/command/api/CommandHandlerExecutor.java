package com.damdamdeo.eventdataspreader.writeside.command.api;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ApplicationScoped
public class CommandHandlerExecutor {

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

    public Optional<AggregateRoot> execute(final Command command) throws Throwable {
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
                final Optional<AggregateRoot> aggregateRoot = executeCommand(command);
                aggregateRoot.ifPresent(ar -> {
                    this.handledAggregateRootIdsInExactlyOnce.remove(ar.aggregateRootId());
                });
                return aggregateRoot;
            }).get();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        } catch (final ExecutionException e) {
            throw e.getCause();
        }
    }

    private class CommandQualifierLiteral extends AnnotationLiteral<CommandQualifier> implements CommandQualifier {

        private final Class value;

        private CommandQualifierLiteral(final Class value) {
            this.value = value;
        }

        @Override
        public Class value() {
            return value;
        }

    }

    private Optional<AggregateRoot> executeCommand(final Command command) {
        final Instance<CommandHandler> commandHandler = CDI.current()
                .select(CommandHandler.class, new CommandQualifierLiteral(command.getClass()));
        if (commandHandler.isResolvable()) {
            final AggregateRoot aggregateRoot = commandHandler.get().handle(command);
            return Optional.of(aggregateRoot);
        } else if (commandHandler.isUnsatisfied()) {
//            TODO log
        } else if (commandHandler.isAmbiguous()) {
            throw new IllegalStateException("Ambigous command handlers for " + command.getClass());
        }
        return Optional.empty();
    }

}
