package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class JacksonStringEncryptionDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(final JsonParser jsonParser,
                              final DeserializationContext deserializationContext) throws IOException {
        @SuppressWarnings("unchecked")
        final Encryption encryption = (Encryption) deserializationContext.getAttribute(Secret.ENCRYPTION_STRATEGY);
        @SuppressWarnings("unchecked")
        final Secret secret = (Secret) deserializationContext.getAttribute(Secret.SECRET_KEY);
        final String value = jsonParser.getValueAsString();
        return secret.decrypt(value, encryption);
    }

}
