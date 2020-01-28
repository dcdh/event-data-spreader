package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayload;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayloadSerializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SerializationException;
import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.JacksonEncryptionSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JacksonEventPayloadSerializer implements EventPayloadSerializer {

    private final ObjectMapper OBJECT_MAPPER;

    public JacksonEventPayloadSerializer(final JacksonEventPayloadSubtypes jacksonEventPayloadSubtypesBean) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.registerSubtypes(jacksonEventPayloadSubtypesBean.namedTypes());
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public String serialize(final EncryptedEventSecret encryptedEventSecret, final EventPayload eventPayload) {
        try {
            return OBJECT_MAPPER
                    .writer()
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret.secret())
                    .writeValueAsString(eventPayload);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public EventPayload deserialize(final EncryptedEventSecret encryptedEventSecret, final String eventPayload) {
        try {
            return OBJECT_MAPPER
                    .readerFor(EventPayload.class)
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret.secret())
                    .readValue(eventPayload);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
