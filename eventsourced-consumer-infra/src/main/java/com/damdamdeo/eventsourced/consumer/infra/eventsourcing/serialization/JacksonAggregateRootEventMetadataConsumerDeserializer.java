package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventMetadataConsumer;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventMetadataConsumerDeserializer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventMetadataConsumerAggregateRootImplementationDiscovery;
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
public class JacksonAggregateRootEventMetadataConsumerDeserializer implements AggregateRootEventMetadataConsumerDeserializer {

    private final ObjectMapper OBJECT_MAPPER;
    private final Encryption encryption;

    public JacksonAggregateRootEventMetadataConsumerDeserializer(final JacksonAggregateRootEventMetadataConsumerAggregateRootImplementationDiscovery jacksonAggregateRootEventMetadataConsumerImplementationDiscoveryBean,
                                                                 final Encryption encryption) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.addMixIn(AggregateRootEventMetadataConsumer.class, JacksonAggregateRootEventMetadataConsumer.class);
        jacksonAggregateRootEventMetadataConsumerImplementationDiscoveryBean.registerJacksonDynamicImplementations(OBJECT_MAPPER);
        this.encryption = Objects.requireNonNull(encryption);
    }

    @Override
    public AggregateRootEventMetadataConsumer deserialize(final Optional<AggregateRootSecret> aggregateRootSecret,
                                                          final String eventConsumerMetadata) {
        try {
            return OBJECT_MAPPER
                    .readerFor(AggregateRootEventMetadataConsumer.class)// FCK
                    .withAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY, encryption)
                    .withAttribute(AggregateRootSecret.SECRET_KEY, aggregateRootSecret)
                    .readValue(eventConsumerMetadata);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
