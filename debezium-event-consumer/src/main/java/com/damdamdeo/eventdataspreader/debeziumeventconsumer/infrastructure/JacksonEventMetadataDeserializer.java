package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadata;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadataDeserializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonEventMetadataSubtypes;
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
public class JacksonEventMetadataDeserializer implements EventMetadataDeserializer {

    private final ObjectMapper OBJECT_MAPPER;

    public JacksonEventMetadataDeserializer(final JacksonEventMetadataSubtypes jacksonEventMetadataSubtypesBean) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.registerSubtypes(jacksonEventMetadataSubtypesBean.namedTypes());
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
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
