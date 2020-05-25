package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventPayloadConsumerDeserializer;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi.JacksonAggregateRootEventPayloadConsumerImplementationDiscovery;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SerializationException;
import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.JacksonEncryptionSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class JacksonAggregateRootEventPayloadConsumerDeserializer implements AggregateRootEventPayloadConsumerDeserializer {

    private final ObjectMapper OBJECT_MAPPER;

    public JacksonAggregateRootEventPayloadConsumerDeserializer(final JacksonAggregateRootEventPayloadConsumerImplementationDiscovery jacksonAggregateRootEventPayloadConsumerImplementationDiscoveryBean) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.addMixIn(AggregateRootEventPayloadConsumer.class, JacksonAggregateRootEventPayloadConsumer.class);
        jacksonAggregateRootEventPayloadConsumerImplementationDiscoveryBean.registerJacksonDynamicImplementations(OBJECT_MAPPER);
    }

    @Override
    public AggregateRootEventPayloadConsumer deserialize(final Optional<EncryptedEventSecret> encryptedEventSecret, final String eventPayload) {
        try {
            return OBJECT_MAPPER
                    .readerFor(AggregateRootEventPayloadConsumer.class)
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret)
                    .readValue(eventPayload);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
