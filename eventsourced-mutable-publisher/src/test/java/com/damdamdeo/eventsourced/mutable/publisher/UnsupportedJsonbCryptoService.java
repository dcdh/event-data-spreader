package com.damdamdeo.eventsourced.mutable.publisher;

import com.damdamdeo.eventsourced.encryption.api.JsonbCryptoService;
import com.damdamdeo.eventsourced.encryption.api.UnableToEncryptMissingSecretException;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.math.BigDecimal;
import java.math.BigInteger;

@ApplicationScoped
public class UnsupportedJsonbCryptoService implements JsonbCryptoService {

    @Override
    public JsonValue encrypt(final AggregateRootId aggregateRootId, final String valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public JsonValue encrypt(final AggregateRootId aggregateRootId, final Long valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public JsonValue encrypt(final AggregateRootId aggregateRootId, final Integer valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public JsonValue encrypt(final AggregateRootId aggregateRootId, final BigInteger valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public JsonValue encrypt(final AggregateRootId aggregateRootId, final BigDecimal valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public JsonValue decrypt(final JsonValue valueDecryptable) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public JsonObject recursiveDecrypt(final JsonObject source) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

}
