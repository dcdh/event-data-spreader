package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventMetadataConsumer;
import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventMetadataConsumerDeserializer;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi.JacksonAggregateRootEventMetadataConsumerImplementationDiscovery;
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
public class JacksonAggregateRootEventMetadataConsumerDeserializer implements AggregateRootEventMetadataConsumerDeserializer {

    private final ObjectMapper OBJECT_MAPPER;

    public JacksonAggregateRootEventMetadataConsumerDeserializer(final JacksonAggregateRootEventMetadataConsumerImplementationDiscovery jacksonAggregateRootEventMetadataConsumerImplementationDiscoveryBean) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.addMixIn(AggregateRootEventMetadataConsumer.class, JacksonAggregateRootEventMetadataConsumer.class);
        jacksonAggregateRootEventMetadataConsumerImplementationDiscoveryBean.registerJacksonDynamicImplementations(OBJECT_MAPPER);
    }

    @Override
    public AggregateRootEventMetadataConsumer deserialize(final Optional<EncryptedEventSecret> encryptedEventSecret, final String eventConsumerMetadata) {
        try {
            return OBJECT_MAPPER
                    .readerFor(AggregateRootEventMetadataConsumer.class)// FCK
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret)
                    .readValue(eventConsumerMetadata);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
