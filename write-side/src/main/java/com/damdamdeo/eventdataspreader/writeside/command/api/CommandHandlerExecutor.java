package com.damdamdeo.eventdataspreader.writeside.command.api;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class CommandHandlerExecutor {

    ExecutorService exactlyOnceCommandExecutor;

    @Inject
    @Any
    Instance<CommandHandler> commandHandlers;

    @PostConstruct
    public void init() {
        this.exactlyOnceCommandExecutor = Executors.newSingleThreadExecutor();
    }

    @PreDestroy
    public void destroy() {
        this.exactlyOnceCommandExecutor.shutdown();
    }

    public Optional<AggregateRoot> execute(final Command command) throws Throwable {
        return exactlyOnceCommandExecutor.submit(() -> executeCommand(command)).get();
    }

    public static class CommandQualifierLiteral extends AnnotationLiteral<CommandQualifier> implements CommandQualifier {

        private final Class value;

        public CommandQualifierLiteral(final Class value) {
            this.value = value;
        }

        @Override
        public Class value() {
            return value;
        }

    }

    private Optional<AggregateRoot> executeCommand(final Command command) {
        final Instance<CommandHandler> commandHandler = commandHandlers
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
