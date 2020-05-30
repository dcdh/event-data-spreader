package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.mockito.Mockito.*;

// The CommandExecutorInterceptor is not an injectable mock. I add to rely on commandExecutor and check if it is called instead

@QuarkusTest
public class CommandExecutorInterceptorTest {

    @InjectMock
    CommandExecutor commandExecutor;

    @Inject
    CommandHandler commandHandler;

    @CommandExecutorBinding
    @ApplicationScoped
    public static class CommandHandler {

        public void execute() {}

    }

    @Test
    public void should_execute_command_executor_when_interceptor_execute() throws Throwable {
        // Given

        // When
        commandHandler.execute();

        // Then
        verify(commandExecutor, times(1)).execute(any());
    }

}
