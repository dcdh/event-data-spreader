package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.Objects;

@Interceptor
@CommandExecutorBinding
public class CommandExecutorInterceptor {

    final CommandExecutor commandExecutor;

    public CommandExecutorInterceptor(final CommandExecutor commandExecutor) {
        this.commandExecutor = Objects.requireNonNull(commandExecutor);
    }

    @AroundInvoke
    public Object execute(final InvocationContext context)  throws Throwable {
        return this.commandExecutor.execute(() -> context.proceed());
    }

}
