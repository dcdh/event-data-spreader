package com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.materializedstate;

import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.command.DebitAccountCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootMaterializedStateSerializer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class JacksonAccountMaterializedStateTest {

    @Inject
    JacksonAggregateRootMaterializedStateSerializer jacksonAggregateRootMaterializedStateSerializer;

    @Test
    public void should_serialize() {
        // Given
        final AccountAggregateRoot accountAggregateRoot = new AccountAggregateRoot();
        accountAggregateRoot.handle(new DebitAccountCommand("owner", BigDecimal.TEN, "executedBy"));

        // When
        final String serialized = jacksonAggregateRootMaterializedStateSerializer.serialize(Optional.empty(), accountAggregateRoot);

        // Then
        assertEquals("{\"@type\":\"AccountMaterializedState\",\"aggregateRootId\":\"owner\",\"version\":0,\"aggregateRootType\":\"AccountAggregateRoot\",\"owner\":\"owner\",\"balance\":990}", serialized);
    }

}
