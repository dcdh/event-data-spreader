package com.damdamdeo.eventdataspreader.event.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.EventMetadata;
import com.damdamdeo.eventdataspreader.event.api.EventMetadataDeSerializer;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonEventMetadataSubtypes;
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
public class JacksonEventMetadataDeSerializer implements EventMetadataDeSerializer {

    private final ObjectMapper OBJECT_MAPPER;

    public JacksonEventMetadataDeSerializer(final JacksonEventMetadataSubtypes jacksonEventMetadataSubtypesBean) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.registerSubtypes(jacksonEventMetadataSubtypesBean.namedTypes());
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public String serialize(final Optional<EncryptedEventSecret> encryptedEventSecret, final EventMetadata eventMetadata) {
        try {
            return OBJECT_MAPPER
                    .writer()
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret)
                    .writeValueAsString(eventMetadata);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public EventMetadata deserialize(final Optional<EncryptedEventSecret> encryptedEventSecret, final String eventMetadata) {
        try {
            return OBJECT_MAPPER
                    .readerFor(EventMetadata.class)
                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret)
                    .readValue(eventMetadata);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}
