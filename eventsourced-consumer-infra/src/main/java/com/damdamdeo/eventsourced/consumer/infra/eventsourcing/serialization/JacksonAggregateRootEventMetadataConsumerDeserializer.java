package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventMetadataConsumer;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventMetadataConsumerDeserializer;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.UnsupportedAggregateRootEventMetadataConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventMetadataConsumerMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SerializationException;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class JacksonAggregateRootEventMetadataConsumerDeserializer implements AggregateRootEventMetadataConsumerDeserializer {

    private final ObjectMapper OBJECT_MAPPER;
    private final Encryption encryption;

    public JacksonAggregateRootEventMetadataConsumerDeserializer(final JacksonAggregateRootEventMetadataConsumerMixInSubtypeDiscovery jacksonAggregateRootEventMetadataConsumerImplementationDiscoveryBean,
                                                                 final Encryption encryption) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.addMixIn(AggregateRootEventMetadataConsumer.class, JacksonAggregateRootEventMetadataConsumer.class);
        OBJECT_MAPPER.addMixIn(UnsupportedAggregateRootEventMetadataConsumer.class, JacksonUnsupportedAggregateRootEventMetadataConsumer.class);
        jacksonAggregateRootEventMetadataConsumerImplementationDiscoveryBean.registerJacksonMixInSubtype(OBJECT_MAPPER);
        this.encryption = Objects.requireNonNull(encryption);
    }

    @Override
    public AggregateRootEventMetadataConsumer deserialize(final Secret secret,
                                                          final String eventConsumerMetadata) {
        try {
            return OBJECT_MAPPER
                    .readerFor(AggregateRootEventMetadataConsumer.class)
                    .withAttribute(Secret.ENCRYPTION_STRATEGY, encryption)
                    .withAttribute(Secret.SECRET_KEY, secret)
                    .readValue(eventConsumerMetadata);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
