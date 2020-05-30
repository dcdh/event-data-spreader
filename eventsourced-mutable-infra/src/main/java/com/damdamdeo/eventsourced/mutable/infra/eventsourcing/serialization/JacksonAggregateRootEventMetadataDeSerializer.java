package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SerializationException;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventMetadata;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventMetadataDeSerializer;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventMetadataImplementationDiscovery;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class JacksonAggregateRootEventMetadataDeSerializer implements AggregateRootEventMetadataDeSerializer {

    private final ObjectMapper OBJECT_MAPPER;
    private final Encryption encryption;

    public JacksonAggregateRootEventMetadataDeSerializer(final JacksonAggregateRootEventMetadataImplementationDiscovery jacksonAggregateRootEventMetadataImplementationDiscovery,
                                                         final Encryption encryption) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.addMixIn(AggregateRootEventMetadata.class, JacksonAggregateRootEventMetadata.class);
        jacksonAggregateRootEventMetadataImplementationDiscovery.registerJacksonDynamicImplementations(OBJECT_MAPPER);
        this.encryption = Objects.requireNonNull(encryption);
    }

    @Override
    public String serialize(final Optional<AggregateRootSecret> aggregateRootSecret, final AggregateRootEventMetadata aggregateRootEventMetadata) {
        try {
            return OBJECT_MAPPER
                    .writer()
                    .withAttribute(AggregateRootSecret.SECRET_KEY, aggregateRootSecret)
                    .withAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY, encryption)
                    .writeValueAsString(aggregateRootEventMetadata);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public AggregateRootEventMetadata deserialize(final Optional<AggregateRootSecret> aggregateRootSecret, final String aggregateRootEventMetadata) {
        try {
            return OBJECT_MAPPER
                    .readerFor(AggregateRootEventMetadata.class)
                    .withAttribute(AggregateRootSecret.SECRET_KEY, aggregateRootSecret)
                    .withAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY, encryption)
                    .readValue(aggregateRootEventMetadata);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
