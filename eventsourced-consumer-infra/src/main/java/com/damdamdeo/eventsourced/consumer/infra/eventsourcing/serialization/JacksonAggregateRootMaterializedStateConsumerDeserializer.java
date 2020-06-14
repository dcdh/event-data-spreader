package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootMaterializedStateConsumer;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootMaterializedStateConsumerDeserializer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery;
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
public class JacksonAggregateRootMaterializedStateConsumerDeserializer implements AggregateRootMaterializedStateConsumerDeserializer {

    private final ObjectMapper OBJECT_MAPPER;
    private final Encryption encryption;

    public JacksonAggregateRootMaterializedStateConsumerDeserializer(final JacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery jacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery,
                                                                     final Encryption encryption) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.addMixIn(AggregateRootMaterializedStateConsumer.class, JacksonAggregateRootMaterializedStateConsumer.class);
        jacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery.registerJacksonMixInSubtype(OBJECT_MAPPER);
        this.encryption = Objects.requireNonNull(encryption);
    }

    @Override
    public AggregateRootMaterializedStateConsumer deserialize(Optional<AggregateRootSecret> aggregateRootSecret, String materializedState) {
        try {
            return OBJECT_MAPPER
                    .readerFor(AggregateRootMaterializedStateConsumer.class)
                    .withAttribute(AggregateRootSecret.SECRET_KEY, aggregateRootSecret)
                    .withAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY, encryption)
                    .readValue(materializedState);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
