package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command;

import com.damdamdeo.eventsourced.mutable.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.eventsourcing.command.Command;
import com.damdamdeo.eventsourced.mutable.eventsourcing.command.CommandHandler;
import com.damdamdeo.eventsourced.mutable.eventsourcing.command.CommandLockingType;
import com.hazelcast.core.HazelcastInstance;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@QuarkusTest
public class CommandExecutorTest {

    @Inject
    CommandExecutor commandExecutor;

    @Inject
    HazelcastInstance hazelcastClient;

    @Inject
    AggregateOnlyLockCommandHandler aggregateOnlyLockCommandHandler;

    @Inject
    GlobalLockCommandHandler globalLockCommandHandler;

    public static class AggregateOnlyLockCommand implements Command {

        @Override
        public CommandLockingType commandLockingType() {
            return CommandLockingType.AGGREGATE_ONLY;
        }

        @Override
        public String aggregateRootId() {
            return "aggregateRootId";
        }

    }

    @ApplicationScoped
    public static class AggregateOnlyLockCommandHandler implements CommandHandler<AggregateRoot, AggregateOnlyLockCommand> {

        private final HazelcastInstance hazelcastClient;

        public AggregateOnlyLockCommandHandler(final HazelcastInstance hazelcastClient) {
            this.hazelcastClient = Objects.requireNonNull(hazelcastClient);
        }

        @CommandExecutorBinding
        @Override
        public AggregateRoot execute(final AggregateOnlyLockCommand command) throws Throwable {
            assertThat(hazelcastClient.getCPSubsystem().getLock("aggregateRootId").isLocked(), equalTo(true));
            assertThat(hazelcastClient.getCPSubsystem().getLock("one-time-command-execution").isLocked(), equalTo(false));
            return null;
        }
    }

    public static class GlobalLockCommand implements Command {

        @Override
        public CommandLockingType commandLockingType() {
            return CommandLockingType.GLOBAL;
        }

        @Override
        public String aggregateRootId() {
            return "aggregateRootId";
        }
    }

    @ApplicationScoped
    public static class GlobalLockCommandHandler implements CommandHandler<AggregateRoot, GlobalLockCommand> {

        private final HazelcastInstance hazelcastClient;

        public GlobalLockCommandHandler(final HazelcastInstance hazelcastClient) {
            this.hazelcastClient = Objects.requireNonNull(hazelcastClient);
        }

        @CommandExecutorBinding
        @Override
        public AggregateRoot execute(GlobalLockCommand command) throws Throwable {
            assertThat(hazelcastClient.getCPSubsystem().getLock("aggregateRootId").isLocked(), equalTo(true));
            assertThat(hazelcastClient.getCPSubsystem().getLock("one-time-command-execution").isLocked(), equalTo(true));
            return null;
        }
    }

    @Test
    public void should_execute_apply_global_lock_and_aggregate_root_id_lock_on_global_lock_command_handler_and_next_unlock_all() throws Throwable {
        // Given

        // When && Then
        globalLockCommandHandler.execute(new GlobalLockCommand());
        assertThat(hazelcastClient.getCPSubsystem().getLock("aggregateRootId").isLocked(), equalTo(false));
        assertThat(hazelcastClient.getCPSubsystem().getLock("one-time-command-execution").isLocked(), equalTo(false));
    }

    @Test
    public void should_execute_apply_aggregate_root_id_lock_on_aggregate_root_lock_command_handler_and_next_unlock_all() throws Throwable {
        // Given

        // When && Then
        aggregateOnlyLockCommandHandler.execute(new AggregateOnlyLockCommand());
        assertThat(hazelcastClient.getCPSubsystem().getLock("aggregateRootId").isLocked(), equalTo(false));
        assertThat(hazelcastClient.getCPSubsystem().getLock("one-time-command-execution").isLocked(), equalTo(false));
    }

    // TODO should unlock after an exception is thrown
    // Je devrais passer par du @MockBean

}
