package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.CommandExecutor;
import io.quarkus.arc.AlternativePriority;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import static org.mockito.Mockito.spy;

@ApplicationScoped
public class TestBeansProducers {

    private final CommandExecutor commandExecutor;

    private TestBeansProducers() {
        commandExecutor = spy(new CommandExecutor());
    }

    @Produces
    @AlternativePriority(1)
    public CommandExecutor commandExecutor() {
        return commandExecutor;
    }

}
