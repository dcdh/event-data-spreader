package com.damdamdeo.eventdataspreader.writeside.command.api;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Instance;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommandHandlerExecutorTest {

    @Test
    public void should_initialise_exactlyOnceCommandExecutor() {
        // Given
        final CommandHandlerExecutor commandHandlerExecutor = new CommandHandlerExecutor();

        // When
        commandHandlerExecutor.init();

        // Then
        assertNotNull(commandHandlerExecutor.exactlyOnceCommandExecutor);
    }

    @Test
    public void should_execute_resolvable_command_handler() throws Throwable {
        // Given
        final CommandHandlerExecutor commandHandlerExecutor = new CommandHandlerExecutor();
        commandHandlerExecutor.init();

        final Command command = mock(Command.class);
        final Instance<CommandHandler> commandHandlerInstance = mock(Instance.class);
        doReturn(true).when(commandHandlerInstance).isResolvable();
        final CommandHandler commandHandler = mock(CommandHandler.class);
        doReturn(commandHandler).when(commandHandlerInstance).get();
        doReturn(mock(AggregateRoot.class)).when(commandHandler).handle(command);
        commandHandlerExecutor.commandHandlers = mock(Instance.class);
        doReturn(commandHandlerInstance).when(commandHandlerExecutor.commandHandlers).select(CommandHandler.class, new CommandHandlerExecutor.CommandQualifierLiteral(command.getClass()));

        // When
        commandHandlerExecutor.execute(command);

        // Then
        verify(commandHandler, atLeastOnce()).handle(command);
        verify(commandHandlerInstance, atLeastOnce()).isResolvable();
        verify(commandHandlerInstance, atLeastOnce()).get();
    }

    @Test
    public void should_not_execute_unsatisfied_command_handler() throws Throwable {
        // Given
        final CommandHandlerExecutor commandHandlerExecutor = new CommandHandlerExecutor();
        commandHandlerExecutor.init();

        final Command command = mock(Command.class);
        final Instance<CommandHandler> commandHandlerInstance = mock(Instance.class);
        doReturn(false).when(commandHandlerInstance).isResolvable();
        doReturn(true).when(commandHandlerInstance).isUnsatisfied();
        final CommandHandler commandHandler = mock(CommandHandler.class);
        doReturn(commandHandler).when(commandHandlerInstance).get();
        doReturn(mock(AggregateRoot.class)).when(commandHandler).handle(command);
        commandHandlerExecutor.commandHandlers = mock(Instance.class);
        doReturn(commandHandlerInstance).when(commandHandlerExecutor.commandHandlers).select(CommandHandler.class, new CommandHandlerExecutor.CommandQualifierLiteral(command.getClass()));

        // When
        commandHandlerExecutor.execute(command);

        // Then
        verify(commandHandler, never()).handle(command);
        verify(commandHandlerInstance, atLeastOnce()).isResolvable();
        verify(commandHandlerInstance, atLeastOnce()).isUnsatisfied();
        verify(commandHandlerInstance, never()).get();
    }

    @Test
    public void should_throw_exception_when_executing_ambiguous_commandHandler() throws Throwable {
        // Given
        final CommandHandlerExecutor commandHandlerExecutor = new CommandHandlerExecutor();
        commandHandlerExecutor.init();

        final Command command = mock(Command.class);
        final Instance<CommandHandler> commandHandlerInstance = mock(Instance.class);
        doReturn(false).when(commandHandlerInstance).isResolvable();
        doReturn(false).when(commandHandlerInstance).isUnsatisfied();
        doReturn(true).when(commandHandlerInstance).isAmbiguous();
        final CommandHandler commandHandler = mock(CommandHandler.class);
        doReturn(commandHandler).when(commandHandlerInstance).get();
        doReturn(mock(AggregateRoot.class)).when(commandHandler).handle(command);
        commandHandlerExecutor.commandHandlers = mock(Instance.class);
        doReturn(commandHandlerInstance).when(commandHandlerExecutor.commandHandlers).select(CommandHandler.class, new CommandHandlerExecutor.CommandQualifierLiteral(command.getClass()));

        // When
        assertThrows(IllegalStateException.class, () -> commandHandlerExecutor.execute(command));

        // Then
        verify(commandHandler, never()).handle(command);
        verify(commandHandlerInstance, atLeastOnce()).isResolvable();
        verify(commandHandlerInstance, atLeastOnce()).isUnsatisfied();
        verify(commandHandlerInstance, atLeastOnce()).isAmbiguous();
        verify(commandHandlerInstance, never()).get();
    }

    @Test
    public void should_shutdown_exactlyOnceCommandExecutor() {
        // Given
        final CommandHandlerExecutor commandHandlerExecutor = new CommandHandlerExecutor();
        commandHandlerExecutor.init();

        // When
        commandHandlerExecutor.destroy();

        // Then
        assertTrue(commandHandlerExecutor.exactlyOnceCommandExecutor.isShutdown());
    }

}
