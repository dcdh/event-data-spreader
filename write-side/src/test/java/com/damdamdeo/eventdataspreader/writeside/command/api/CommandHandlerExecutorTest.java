package com.damdamdeo.eventdataspreader.writeside.command.api;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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
    public void should_initialise_threadPools() {
        // Given
        final CommandHandlerExecutor commandHandlerExecutor = new CommandHandlerExecutor();

        // When
        commandHandlerExecutor.init();

        // Then
        assertNotNull(commandHandlerExecutor.threadPools);
        assertEquals(20, commandHandlerExecutor.threadPools.size());
        commandHandlerExecutor.threadPools.forEach(thread -> assertNotNull(thread));
    }

    @Test
    public void should_exactlyOnceCommandExecution_be_executed_on_exactlyOnceCommandExecutor() throws Throwable {
        // Given
        final CommandHandlerExecutor commandHandlerExecutor = new CommandHandlerExecutor();
        final Command command = mock(Command.class);
        doReturn(true).when(command).exactlyOnceCommandExecution();
        doReturn("aggregateId").when(command).aggregateId();
        commandHandlerExecutor.commandHandlers = mock(Instance.class);
        final Instance<CommandHandler> commandHandlerInstance = mock(Instance.class);
        doReturn(commandHandlerInstance).when(commandHandlerExecutor.commandHandlers).select(CommandHandler.class, new CommandHandlerExecutor.CommandQualifierLiteral(command.getClass()));
        commandHandlerExecutor.exactlyOnceCommandExecutor = mock(ExecutorService.class);
        doReturn(mock(Future.class)).when(commandHandlerExecutor.exactlyOnceCommandExecutor).submit(any(Callable.class));
        commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce = new HashSet<>();

        // When
        commandHandlerExecutor.execute(command);

        // Then
        assertEquals(1, commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce.size());
        assertTrue(commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce.contains("aggregateId"));
        verify(command, atLeastOnce()).aggregateId();
        verify(commandHandlerExecutor.exactlyOnceCommandExecutor).submit(any(Callable.class));
        verify(command, atLeastOnce()).exactlyOnceCommandExecution();
    }

    @Test
    public void should_not_exactlyOnceCommandExecution_previously_executed_on_exactlyOnceCommandExecution_be_executed_on_exactlyOnceCommandExecution() throws Throwable {
        // Given
        final CommandHandlerExecutor commandHandlerExecutor = new CommandHandlerExecutor();
        final Command command = mock(Command.class);
        doReturn(false).when(command).exactlyOnceCommandExecution();
        doReturn("aggregateId").when(command).aggregateId();
        commandHandlerExecutor.commandHandlers = mock(Instance.class);
        final Instance<CommandHandler> commandHandlerInstance = mock(Instance.class);
        doReturn(commandHandlerInstance).when(commandHandlerExecutor.commandHandlers).select(CommandHandler.class, new CommandHandlerExecutor.CommandQualifierLiteral(command.getClass()));
        commandHandlerExecutor.exactlyOnceCommandExecutor = mock(ExecutorService.class);
        doReturn(mock(Future.class)).when(commandHandlerExecutor.exactlyOnceCommandExecutor).submit(any(Callable.class));
        commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce = new HashSet<>();
        commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce.add("aggregateId");

        // When
        commandHandlerExecutor.execute(command);

        // Then
        assertEquals(1, commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce.size());
        assertTrue(commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce.contains("aggregateId"));
        verify(command, atLeastOnce()).aggregateId();
        verify(commandHandlerExecutor.exactlyOnceCommandExecutor).submit(any(Callable.class));
        verify(command, atLeastOnce()).exactlyOnceCommandExecution();
    }

    @Test
    public void should_execute_on_first_thread_pool_when_command_aggregate_root_is_null() throws Throwable {
        // Given
        final CommandHandlerExecutor commandHandlerExecutor = new CommandHandlerExecutor();
        final Command command = mock(Command.class);
        doReturn(false).when(command).exactlyOnceCommandExecution();
        doReturn(null).when(command).aggregateId();
        commandHandlerExecutor.commandHandlers = mock(Instance.class);
        final Instance<CommandHandler> commandHandlerInstance = mock(Instance.class);
        doReturn(commandHandlerInstance).when(commandHandlerExecutor.commandHandlers).select(CommandHandler.class, new CommandHandlerExecutor.CommandQualifierLiteral(command.getClass()));
        final ExecutorService executorService = mock(ExecutorService.class);
        doReturn(mock(Future.class)).when(executorService).submit(any(Callable.class));
        commandHandlerExecutor.threadPools = Collections.singletonList(executorService);
        commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce = new HashSet<>();

        // When
        commandHandlerExecutor.execute(command);

        // Then
        assertEquals(0, commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce.size());
        verify(command, atLeastOnce()).aggregateId();
        verify(commandHandlerExecutor.threadPools.get(0)).submit(any(Callable.class));
    }

    @Test
    public void should_execute_on_thread_index_1_when_aggregateId_is_aggregateId_and_3_threads_in_pool() throws Throwable {
        // Given
        Validate.validState(Math.abs("aggregateId".hashCode()) % 3 == 1);
        final CommandHandlerExecutor commandHandlerExecutor = new CommandHandlerExecutor();
        final Command command = mock(Command.class);
        doReturn(false).when(command).exactlyOnceCommandExecution();
        doReturn("aggregateId").when(command).aggregateId();
        commandHandlerExecutor.commandHandlers = mock(Instance.class);
        final Instance<CommandHandler> commandHandlerInstance = mock(Instance.class);
        doReturn(commandHandlerInstance).when(commandHandlerExecutor.commandHandlers).select(CommandHandler.class, new CommandHandlerExecutor.CommandQualifierLiteral(command.getClass()));
        commandHandlerExecutor.threadPools = new ArrayList<>();
        commandHandlerExecutor.threadPools.add(mock(ExecutorService.class));
        commandHandlerExecutor.threadPools.add(mock(ExecutorService.class));
        commandHandlerExecutor.threadPools.add(mock(ExecutorService.class));
        doReturn(mock(Future.class)).when(commandHandlerExecutor.threadPools.get(1)).submit(any(Callable.class));
        commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce = new HashSet<>();

        // When
        commandHandlerExecutor.execute(command);

        // Then
        assertEquals(0, commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce.size());
        verify(command, atLeastOnce()).aggregateId();
        verify(commandHandlerExecutor.threadPools.get(1)).submit(any(Callable.class));
    }

    @Test
    public void should_execute_resolvable_command_handler() throws Throwable {
        // Given
        final CommandHandlerExecutor commandHandlerExecutor = new CommandHandlerExecutor();
        commandHandlerExecutor.init();

        final Command command = mock(Command.class);
        doReturn(true).when(command).exactlyOnceCommandExecution();
        doReturn("aggregateId").when(command).aggregateId();
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
        assertEquals(0, commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce.size());
        verify(command, atLeastOnce()).aggregateId();
        verify(command, atLeastOnce()).exactlyOnceCommandExecution();
        verify(commandHandlerInstance, atLeastOnce()).isResolvable();
        verify(commandHandlerInstance, atLeastOnce()).get();
    }

    @Test
    public void should_not_execute_unsatisfied_command_handler() throws Throwable {
        // Given
        final CommandHandlerExecutor commandHandlerExecutor = new CommandHandlerExecutor();
        commandHandlerExecutor.init();

        final Command command = mock(Command.class);
        doReturn(true).when(command).exactlyOnceCommandExecution();
        doReturn("aggregateId").when(command).aggregateId();
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
        assertEquals(0, commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce.size());
        verify(command, atLeastOnce()).aggregateId();
        verify(command, atLeastOnce()).exactlyOnceCommandExecution();
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
        doReturn(true).when(command).exactlyOnceCommandExecution();
        doReturn("aggregateId").when(command).aggregateId();
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
        assertEquals(1, commandHandlerExecutor.handledAggregateRootIdsInExactlyOnce.size());// valeur 1 tendancieux ...
        verify(command, atLeastOnce()).aggregateId();
        verify(command, atLeastOnce()).exactlyOnceCommandExecution();
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

    @Test
    public void should_shutdown_threadPools() {
        // Given
        final CommandHandlerExecutor commandHandlerExecutor = new CommandHandlerExecutor();
        commandHandlerExecutor.init();

        // When
        commandHandlerExecutor.destroy();

        // Then
        commandHandlerExecutor.threadPools.forEach(thread -> assertTrue(thread.isShutdown()));
    }

}
