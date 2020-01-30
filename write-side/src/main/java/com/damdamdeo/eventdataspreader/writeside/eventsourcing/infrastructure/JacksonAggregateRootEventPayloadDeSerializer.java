package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.JacksonEncryptionSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootEventPayloadSubtypes;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JacksonAggregateRootEventPayloadDeSerializer implements AggregateRootEventPayloadDeSerializer {

    private final ObjectMapper OBJECT_MAPPER;

    public JacksonAggregateRootEventPayloadDeSerializer(final JacksonAggregateRootEventPayloadSubtypes jacksonAggregateRootEventPayloadSubtypesBean) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.registerSubtypes(jacksonAggregateRootEventPayloadSubtypesBean.namedTypes());
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public String serialize(final EncryptedEventSecret encryptedEventSecret, final AggregateRootEventPayload aggregateRootEventPayload) {
        try {
            return OBJECT_MAPPER
                    .writer()
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret.secret())
                    .writeValueAsString(aggregateRootEventPayload);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public AggregateRootEventPayload deserialize(final EncryptedEventSecret encryptedEventSecret, final String eventPayload) {
        try {
            return OBJECT_MAPPER
                    .readerFor(AggregateRootEventPayload.class)
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret.secret())
                    .readValue(eventPayload);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
