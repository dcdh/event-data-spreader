package com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.materializedstate;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootMaterializedStateSerializer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class JacksonGiftMaterializedStateTest {

    @Inject
    JacksonAggregateRootMaterializedStateSerializer jacksonAggregateRootMaterializedStateSerializer;

    @Test
    public void should_serialize() {
        // Given
        final GiftAggregateRoot giftAggregateRoot = new GiftAggregateRoot();
        giftAggregateRoot.handle(new BuyGiftCommand("name", "executedBy"));

        // When
        final String serialized = jacksonAggregateRootMaterializedStateSerializer.serialize(Optional.empty(), giftAggregateRoot);

        // Then
        assertEquals("{\"@type\":\"GiftMaterializedState\",\"aggregateRootId\":\"name\",\"version\":0,\"aggregateRootType\":\"GiftAggregateRoot\",\"name\":\"name\",\"offeredTo\":null}", serialized);
    }

}
