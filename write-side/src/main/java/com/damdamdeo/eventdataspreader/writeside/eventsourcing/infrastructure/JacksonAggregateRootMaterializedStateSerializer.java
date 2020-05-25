package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SerializationException;
import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.JacksonEncryptionSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootMaterializedStateSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootMaterializedStateImplementationDiscovery;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class JacksonAggregateRootMaterializedStateSerializer implements AggregateRootMaterializedStateSerializer {

    private final ObjectMapper OBJECT_MAPPER;

    public JacksonAggregateRootMaterializedStateSerializer(final JacksonAggregateRootMaterializedStateImplementationDiscovery jacksonAggregateRootMaterializedStateImplementationDiscoveryBean) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.addMixIn(AggregateRoot.class, JacksonAggregateRootMaterializedState.class);
        jacksonAggregateRootMaterializedStateImplementationDiscoveryBean.registerJacksonDynamicImplementations(OBJECT_MAPPER);
    }

    @Override
    public String serialize(final Optional<EncryptedEventSecret> encryptedEventSecret, final AggregateRoot aggregateRoot) {
        try {
            return OBJECT_MAPPER
                    .writer()
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret)
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
//                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret)
                    .writeValueAsString(aggregateRoot);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
