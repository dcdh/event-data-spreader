package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SerializationException;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootSubtypes;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

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
    public String serialize(final AggregateRoot aggregateRoot) {
        try {
            return OBJECT_MAPPER
                    .writer()
                    // pas besoin d'utiliser un secret car je considère que la projection n'est pas protégé
                    // si je souhaite injecter un secret il sera commun à la plateforme et ce fera par constructor
                    // tant que j'anonymise pas la donnée en la supprimant
//                    .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, encryptedEventSecret)
                    .writeValueAsString(aggregateRoot);
        } catch (final Exception e) {
            throw new SerializationException(e);
        }
    }

}

