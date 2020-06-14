package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Optional;

public class JacksonEncryptionDeserializer extends JsonDeserializer<String> {

    private static final String ANONYMIZED_VALUE = "*****";

    @Override
    public String deserialize(final JsonParser jsonParser,
                              final DeserializationContext deserializationContext) throws IOException {
        @SuppressWarnings("unchecked")
        final Encryption encryption = (Encryption) deserializationContext.getAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY);
        @SuppressWarnings("unchecked")
        final Optional<AggregateRootSecret> aggregateRootSecret = (Optional<AggregateRootSecret>) deserializationContext
                .getAttribute(AggregateRootSecret.SECRET_KEY);
        final String value = jsonParser.getValueAsString();
        return aggregateRootSecret.map(AggregateRootSecret::secret)
                .map(secret -> encryption.decrypt(value, secret))
                .orElse(ANONYMIZED_VALUE); // secret has been deleted, I cannot decode it. RGPD !
    }

}
