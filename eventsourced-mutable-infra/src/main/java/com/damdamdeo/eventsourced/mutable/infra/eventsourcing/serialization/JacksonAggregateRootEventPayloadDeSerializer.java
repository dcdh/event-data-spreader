package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SerializationException;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.UnsupportedAggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventPayloadMixInSubtypeDiscovery;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class JacksonAggregateRootEventPayloadDeSerializer implements AggregateRootEventPayloadDeSerializer {

    private final ObjectMapper OBJECT_MAPPER;
    private final Encryption encryption;

    public JacksonAggregateRootEventPayloadDeSerializer(final JacksonAggregateRootEventPayloadMixInSubtypeDiscovery jacksonAggregateRootEventPayloadMixInSubtypeDiscovery,
                                                        final Encryption encryption) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.addMixIn(AggregateRootEventPayload.class, JacksonAggregateRootEventPayload.class);
        OBJECT_MAPPER.addMixIn(UnsupportedAggregateRootEventPayload.class, JacksonUnsupportedAggregateRootEventPayload.class);
        jacksonAggregateRootEventPayloadMixInSubtypeDiscovery.registerJacksonMixInSubtype(OBJECT_MAPPER);
        this.encryption = Objects.requireNonNull(encryption);
    }

    @Override
    public String serialize(final Secret secret, final AggregateRootEventPayload aggregateRootEventPayload) {
        try {
            return OBJECT_MAPPER
                    .writer()
                    .withAttribute(Secret.SECRET_KEY, secret)
                    .withAttribute(Secret.ENCRYPTION_STRATEGY, encryption)
                    .writeValueAsString(aggregateRootEventPayload);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public AggregateRootEventPayload deserialize(final Secret secret, final String eventPayload) {
        try {
            return OBJECT_MAPPER
                    .readerFor(AggregateRootEventPayload.class)
                    .withAttribute(Secret.SECRET_KEY, secret)
                    .withAttribute(Secret.ENCRYPTION_STRATEGY, encryption)
                    .readValue(eventPayload);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
