package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SerializationException;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventPayloadImplementationDiscovery;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class JacksonAggregateRootEventPayloadDeSerializer implements AggregateRootEventPayloadDeSerializer {

    private final ObjectMapper OBJECT_MAPPER;
    private final Encryption encryption;

    public JacksonAggregateRootEventPayloadDeSerializer(final JacksonAggregateRootEventPayloadImplementationDiscovery jacksonAggregateRootEventPayloadImplementationDiscoveryBean,
                                                        final Encryption encryption) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.addMixIn(AggregateRootEventPayload.class, JacksonAggregateRootEventPayload.class);
        jacksonAggregateRootEventPayloadImplementationDiscoveryBean.registerJacksonDynamicImplementations(OBJECT_MAPPER);
        this.encryption = Objects.requireNonNull(encryption);
    }

    @Override
    public String serialize(final Optional<AggregateRootSecret> aggregateRootSecret, final AggregateRootEventPayload aggregateRootEventPayload) {
        try {
            return OBJECT_MAPPER
                    .writer()
                    .withAttribute(AggregateRootSecret.SECRET_KEY, aggregateRootSecret)
                    .withAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY, encryption)
                    .writeValueAsString(aggregateRootEventPayload);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public AggregateRootEventPayload deserialize(final Optional<AggregateRootSecret> aggregateRootSecret, final String eventPayload) {
        try {
            return OBJECT_MAPPER
                    .readerFor(AggregateRootEventPayload.class)
                    .withAttribute(AggregateRootSecret.SECRET_KEY, aggregateRootSecret)
                    .withAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY, encryption)
                    .readValue(eventPayload);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
