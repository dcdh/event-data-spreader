package com.damdamdeo.eventdataspreader.event.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.EventPayload;
import com.damdamdeo.eventdataspreader.event.api.EventPayloadDeserializer;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SerializationException;
import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.JacksonEncryptionSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class JacksonEventPayloadDeserializer implements EventPayloadDeserializer {

    private final ObjectMapper OBJECT_MAPPER;

    public JacksonEventPayloadDeserializer(final JacksonEventPayloadSubtypes jacksonEventPayloadSubtypesBean) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.registerSubtypes(jacksonEventPayloadSubtypesBean.namedTypes());
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public EventPayload deserialize(final Optional<EncryptedEventSecret> encryptedEventSecret, final String eventPayload) {
        try {
            return OBJECT_MAPPER
                    .readerFor(EventPayload.class)
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret)
                    .readValue(eventPayload);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
