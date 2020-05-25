package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SerializationException;
import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.JacksonEncryptionSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventMetadataDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootEventMetadataImplementationDiscovery;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class JacksonAggregateRootEventMetadataDeSerializer implements AggregateRootEventMetadataDeSerializer {

    private final ObjectMapper OBJECT_MAPPER;

    public JacksonAggregateRootEventMetadataDeSerializer(final JacksonAggregateRootEventMetadataImplementationDiscovery jacksonAggregateRootEventMetadataImplementationDiscovery) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.addMixIn(AggregateRootEventMetadata.class, JacksonAggregateRootEventMetadata.class);
        jacksonAggregateRootEventMetadataImplementationDiscovery.registerJacksonDynamicImplementations(OBJECT_MAPPER);
    }

    @Override
    public String serialize(final Optional<EncryptedEventSecret> encryptedEventSecret, final AggregateRootEventMetadata aggregateRootEventMetadata) {
        try {
            return OBJECT_MAPPER
                    .writer()
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret)
                    .writeValueAsString(aggregateRootEventMetadata);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public AggregateRootEventMetadata deserialize(final Optional<EncryptedEventSecret> encryptedEventSecret, final String eventConsumerMetadata) {
        try {
            return OBJECT_MAPPER
                    .readerFor(AggregateRootEventMetadata.class)
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret)
                    .readValue(eventConsumerMetadata);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
