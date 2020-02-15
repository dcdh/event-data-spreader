package com.damdamdeo.eventdataspreader.writeside.aggregate;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootSubtypes;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled// FIXME Fail to build using maven !!!
public class GiftAggregateTest {

    private static class DefaultJacksonAggregateRootSubtypes implements JacksonAggregateRootSubtypes {

        @Override
        public List<JacksonSubtype<AggregateRoot>> jacksonSubtypes() {
            return singletonList(new JacksonSubtype<>(GiftAggregate.class, "GiftAggregate"));
        }

    }

    @Test
    public void should_serialize() {
        // Given
        final AggregateRootSerializer aggregateRootSerializer = new JacksonAggregateRootSerializer(new DefaultJacksonAggregateRootSubtypes());
        final GiftAggregate giftAggregate = new GiftAggregate();
        giftAggregate.handle(new BuyGiftCommand("name", "executedBy"));

        // When
        final String serialized = aggregateRootSerializer.serialize(giftAggregate);

        // Then
        assertEquals("{\"@type\":\"GiftAggregate\",\"aggregateRootId\":\"name\",\"name\":\"name\",\"offeredTo\":null,\"version\":0,\"aggregateRootType\":\"GiftAggregate\"}", serialized);
    }

}
