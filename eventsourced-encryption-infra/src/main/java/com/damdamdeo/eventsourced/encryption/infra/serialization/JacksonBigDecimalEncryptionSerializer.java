package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class JacksonBigDecimalEncryptionSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(final BigDecimal value,
                          final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
          @SuppressWarnings("unchecked")
          final Encryption encryption = (Encryption) serializerProvider.getAttribute(Secret.ENCRYPTION_STRATEGY);
          @SuppressWarnings("unchecked")
          final Secret secret = (Secret) serializerProvider.getAttribute(Secret.SECRET_KEY);
          jsonGenerator.writeString(secret.encrypt(value.toString(), encryption));
    }

}
