package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SerializationException;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootMaterializedStateSerializer;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootMaterializedStateMixInSubtypeDiscovery;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class JacksonAggregateRootMaterializedStateSerializer implements AggregateRootMaterializedStateSerializer {

    private final ObjectMapper OBJECT_MAPPER;
    private final Encryption encryption;

    public JacksonAggregateRootMaterializedStateSerializer(final JacksonAggregateRootMaterializedStateMixInSubtypeDiscovery jacksonAggregateRootMaterializedStateMixInSubtypeDiscovery,
                                                           final Encryption encryption) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.addMixIn(AggregateRoot.class, JacksonAggregateRootMaterializedState.class);
        jacksonAggregateRootMaterializedStateMixInSubtypeDiscovery.registerJacksonMixInSubtype(OBJECT_MAPPER);
        this.encryption = Objects.requireNonNull(encryption);
    }

    @Override
    public String serialize(final Optional<AggregateRootSecret> aggregateRootSecret, final AggregateRoot aggregateRoot) {
        try {
            return OBJECT_MAPPER
                    .writer()
                    .withAttribute(AggregateRootSecret.SECRET_KEY, aggregateRootSecret)
                    .withAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY, encryption)
                    .writeValueAsString(aggregateRoot);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public String serialize(final AggregateRoot aggregateRoot) {
        try {
            return OBJECT_MAPPER
                    .writer()
                    .writeValueAsString(aggregateRoot);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
