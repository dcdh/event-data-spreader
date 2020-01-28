package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SerializationException;
import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.JacksonEncryptionSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootSubtypes;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JacksonAggregateRootSerializer implements AggregateRootSerializer {

    private final ObjectMapper OBJECT_MAPPER;

    public JacksonAggregateRootSerializer(final JacksonAggregateRootSubtypes jacksonAggregateRootSubtypesBean) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.registerSubtypes(jacksonAggregateRootSubtypesBean.namedTypes());
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public String serialize(final EncryptedEventSecret encryptedEventSecret, final AggregateRoot aggregateRoot) {
        try {
            return OBJECT_MAPPER
                    .writer()
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret.secret())
                    .writeValueAsString(aggregateRoot);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public AggregateRoot deserialize(final EncryptedEventSecret encryptedEventSecret, final String aggregateRoot) {
        try {
            return OBJECT_MAPPER
                    .readerFor(AggregateRoot.class)
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret.secret())
                    .readValue(aggregateRoot);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }
}

