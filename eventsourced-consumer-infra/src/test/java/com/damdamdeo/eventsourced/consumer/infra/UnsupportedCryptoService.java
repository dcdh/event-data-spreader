package com.damdamdeo.eventsourced.consumer.infra;

import com.damdamdeo.eventsourced.encryption.api.CryptoService;
import com.damdamdeo.eventsourced.encryption.api.UnableToEncryptMissingSecretException;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import java.math.BigDecimal;
import java.math.BigInteger;

@ApplicationScoped
public class UnsupportedCryptoService implements CryptoService<JsonObject> {

    @Override
    public JsonObject encrypt(final AggregateRootId aggregateRootId, final String valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public JsonObject encrypt(final AggregateRootId aggregateRootId, final Long valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public JsonObject encrypt(final AggregateRootId aggregateRootId, final Integer valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public JsonObject encrypt(final AggregateRootId aggregateRootId, final BigInteger valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public JsonObject encrypt(final AggregateRootId aggregateRootId, final BigDecimal valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public JsonObject decrypt(final JsonObject parent, final String childName) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public JsonObject recursiveDecrypt(final JsonObject source) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

}
