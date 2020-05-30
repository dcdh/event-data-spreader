package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.Optional;

public class JacksonEncryptionSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(final String value,
                          final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
        @SuppressWarnings("unchecked")
        final Encryption encryption = (Encryption) serializerProvider.getAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY);
        final Optional<AggregateRootSecret> aggregateRootSecret = (Optional<AggregateRootSecret>) serializerProvider.getAttribute(AggregateRootSecret.SECRET_KEY);
        Validate.validState(aggregateRootSecret.isPresent());
        final String secret = aggregateRootSecret.map(AggregateRootSecret::secret).get();
        jsonGenerator.writeString(encryption.encrypt(value, secret));
    }

}
