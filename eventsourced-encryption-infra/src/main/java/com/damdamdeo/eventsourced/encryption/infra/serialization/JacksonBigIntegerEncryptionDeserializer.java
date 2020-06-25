package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigInteger;

public class JacksonBigIntegerEncryptionDeserializer extends JsonDeserializer<BigInteger> {

    @Override
    public BigInteger deserialize(final JsonParser jsonParser,
                              final DeserializationContext deserializationContext) throws IOException {
        @SuppressWarnings("unchecked")
        final Encryption encryption = (Encryption) deserializationContext.getAttribute(Secret.ENCRYPTION_STRATEGY);
        @SuppressWarnings("unchecked")
        final Secret secret = (Secret) deserializationContext.getAttribute(Secret.SECRET_KEY);
        final String value = jsonParser.getValueAsString();
        return new BigInteger(secret.decrypt(value, encryption));
    }

}
