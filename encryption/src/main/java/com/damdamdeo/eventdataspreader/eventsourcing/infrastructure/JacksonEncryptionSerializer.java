package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.Encryption;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.Validate;

import java.io.IOException;

public class JacksonEncryptionSerializer extends JsonSerializer<String> {

    private static final Encryption ENCRYPTION;
    public static final String ENCODER_SECRET = "encoderSecret";

    static {
        ENCRYPTION = new AESEncryption();
    }

    @Override
    public void serialize(final String value,
                          final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
        final String secret = (String) serializerProvider.getAttribute(ENCODER_SECRET);
        Validate.validState(secret.length() == AESEncryption.SECRET_LENGTH);
        jsonGenerator.writeString(ENCRYPTION.encrypt(value, secret));
    }

}
