package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.Command;

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
        if ("execute".equals(context.getMethod().getName())
                && context.getParameters().length == 1
                && Command.class.isAssignableFrom(context.getParameters()[0].getClass())) {
            return this.commandExecutor.execute(context);
        }
        return context.proceed();
    }

}
