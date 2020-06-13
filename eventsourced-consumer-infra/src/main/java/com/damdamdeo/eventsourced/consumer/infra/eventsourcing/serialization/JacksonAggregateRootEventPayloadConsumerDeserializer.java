package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventPayloadConsumer;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventPayloadConsumerDeserializer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventPayloadConsumerMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SerializationException;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class JacksonAggregateRootEventPayloadConsumerDeserializer implements AggregateRootEventPayloadConsumerDeserializer {

    private final ObjectMapper OBJECT_MAPPER;
    private final Encryption encryption;

    public JacksonAggregateRootEventPayloadConsumerDeserializer(final JacksonAggregateRootEventPayloadConsumerMixInSubtypeDiscovery jacksonAggregateRootEventPayloadConsumerImplementationDiscoveryBean,
                                                                final Encryption encryption) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.addMixIn(AggregateRootEventPayloadConsumer.class, JacksonAggregateRootEventPayloadConsumer.class);
        jacksonAggregateRootEventPayloadConsumerImplementationDiscoveryBean.registerJacksonMixInSubtype(OBJECT_MAPPER);
        this.encryption = Objects.requireNonNull(encryption);
    }

    @Override
    public AggregateRootEventPayloadConsumer deserialize(final Optional<AggregateRootSecret> aggregateRootSecret, final String eventPayload) {
        try {
            return OBJECT_MAPPER
                    .readerFor(AggregateRootEventPayloadConsumer.class)
                    .withAttribute(AggregateRootSecret.SECRET_KEY, aggregateRootSecret)
                    .withAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY, encryption)
                    .readValue(eventPayload);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
