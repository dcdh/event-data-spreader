package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.Encryption;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class JacksonEncryptionDeserializer extends JsonDeserializer<String> {

    private static final Encryption ENCRYPTION;

    private static final String ANONYMIZED_VALUE = "*****";

    static {
        ENCRYPTION = new AESEncryption();
    }

    @Override
    public String deserialize(final JsonParser jsonParser,
                              final DeserializationContext deserializationContext) throws IOException {
        final String secret = (String) deserializationContext.getAttribute(JacksonEncryptionSerializer.ENCODER_SECRET);
        if (secret == null || "".equals(secret)) {
            // secret has been deleted, I cannot decode it. RGPD !
            return ANONYMIZED_VALUE;
        }
        final String encrypted = jsonParser.getValueAsString();
        return ENCRYPTION.decrypt(encrypted, secret);
    }

}
