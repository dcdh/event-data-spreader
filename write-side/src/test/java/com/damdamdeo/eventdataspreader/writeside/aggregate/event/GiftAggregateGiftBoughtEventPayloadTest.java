package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootEventPayloadSubtypes;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GiftAggregateGiftBoughtEventPayloadTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(GiftAggregateGiftBoughtEventPayload.class).verify();
    }

    private static class DefaultJacksonAggregateRootEventPayloadSubtypes implements JacksonAggregateRootEventPayloadSubtypes {

        @Override
        public List<JacksonSubtype<AggregateRootEventPayload>> jacksonSubtypes() {
            return singletonList(new JacksonSubtype<>(GiftAggregateGiftBoughtEventPayload.class, "GiftAggregateGiftBoughtEventPayload"));
        }

    }

    @Test
    public void should_serialize() {
        // Given
        final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer = new JacksonAggregateRootEventPayloadDeSerializer(new DefaultJacksonAggregateRootEventPayloadSubtypes());

        // When
        final String serialized = aggregateRootEventPayloadDeSerializer.serialize(Optional.empty(),
                new GiftAggregateGiftBoughtEventPayload("name"));

        // Then
        assertEquals("{\"@type\":\"GiftAggregateGiftBoughtEventPayload\",\"name\":\"name\"}", serialized);
    }

    @Test
    public void should_deserialize() {
        // Given
        final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer = new JacksonAggregateRootEventPayloadDeSerializer(new DefaultJacksonAggregateRootEventPayloadSubtypes());

        // When
        final AggregateRootEventPayload deserialized = aggregateRootEventPayloadDeSerializer.deserialize(Optional.empty(),
                "{\"@type\":\"GiftAggregateGiftBoughtEventPayload\",\"name\":\"name\"}");

        // Then
        assertEquals(new GiftAggregateGiftBoughtEventPayload("name"), deserialized);
    }

}
