package com.damdamdeo.eventdataspreader.writeside.command.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandExecutorTest {

    final class MyException extends Exception {

    }

    @Test
    public void should_throw_my_exception() {
        // Given
        final CommandExecutor commandExecutor = new CommandExecutor();

        // When && Then
        assertThrows(MyException.class, () -> commandExecutor.execute(() -> {
            throw new MyException();
        }));
    }

}
