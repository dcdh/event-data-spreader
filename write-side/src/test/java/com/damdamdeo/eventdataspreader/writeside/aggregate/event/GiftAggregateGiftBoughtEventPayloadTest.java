package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayloadSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventPayloadSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootEventPayloadSubtypes;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled// FIXME Fail to build using maven !!!
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

    private static class DefaultEncryptedEventSecret implements EncryptedEventSecret {

        @Override
        public String aggregateRootId() {
            return null;
        }

        @Override
        public String aggregateRootType() {
            return null;
        }

        @Override
        public Date creationDate() {
            return null;
        }

        @Override
        public String secret() {
            return null;
        }

    }

    @Test
    public void should_serialize() {
        // Given
        final AggregateRootEventPayloadSerializer aggregateRootEventPayloadSerializer = new JacksonAggregateRootEventPayloadSerializer(new DefaultJacksonAggregateRootEventPayloadSubtypes());

        // When
        final String serialized = aggregateRootEventPayloadSerializer.serialize(new DefaultEncryptedEventSecret(),
                new GiftAggregateGiftBoughtEventPayload("name"));

        // Then
        assertEquals("{\"@type\":\"GiftAggregateGiftBoughtEventPayload\",\"name\":\"name\"}", serialized);
    }

    @Test
    public void should_deserialize() {
        // Given
        final AggregateRootEventPayloadSerializer aggregateRootEventPayloadSerializer = new JacksonAggregateRootEventPayloadSerializer(new DefaultJacksonAggregateRootEventPayloadSubtypes());

        // When
        final AggregateRootEventPayload deserialized = aggregateRootEventPayloadSerializer.deserialize(new DefaultEncryptedEventSecret(),
                "{\"@type\":\"GiftAggregateGiftBoughtEventPayload\",\"name\":\"name\"}");

        // Then
        assertEquals(new GiftAggregateGiftBoughtEventPayload("name"), deserialized);
    }

}
