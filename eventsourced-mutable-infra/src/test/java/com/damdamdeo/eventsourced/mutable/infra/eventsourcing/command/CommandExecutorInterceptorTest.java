package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.Command;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.CommandHandler;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.CommandLockingType;
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
    TestCommandHandler commandHandler;

    public static class TestCommand implements Command {

        @Override
        public CommandLockingType commandLockingType() {
            return null;
        }

        @Override
        public String aggregateRootId() {
            return null;
        }
    }

    @CommandExecutorBinding
    @ApplicationScoped
    public static class TestCommandHandler implements CommandHandler<AggregateRoot, TestCommand> {

        @Override
        public AggregateRoot execute(final TestCommand command) throws Throwable {
            return null;
        }
    }

    @Test
    public void should_execute_command_executor_when_interceptor_execute() throws Throwable {
        // Given

        // When
        commandHandler.execute(new TestCommand());

        // Then
        verify(commandExecutor, times(1)).execute(any());
    }

}
