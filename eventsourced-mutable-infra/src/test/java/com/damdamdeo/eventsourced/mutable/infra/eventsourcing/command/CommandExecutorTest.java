package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@QuarkusTest
public class CommandExecutorTest {

    @Inject
    CommandExecutor commandExecutor;

    @Test
    public void should_execute_callable() throws Throwable {
        // Given
        final Callable<Void> callable = mock(Callable.class);
        final Void givenVoidReturn = mock(Void.class);
        doReturn(givenVoidReturn).when(callable).call();

        // When
        final Void response = commandExecutor.execute(callable);

        // Then
        assertEquals(givenVoidReturn, response);
        verify(callable, times(1)).call();
    }

    final static class MyException extends Exception {

    }

    @Test
    public void should_throw_my_exception_when_callable_thrown_my_exception() {
        // Given
        // When && Then
        assertThrows(MyException.class, () -> commandExecutor.execute(() -> {
            throw new MyException();
        }));
    }

}
