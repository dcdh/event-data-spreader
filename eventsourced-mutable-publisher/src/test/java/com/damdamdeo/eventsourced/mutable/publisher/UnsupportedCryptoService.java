package com.damdamdeo.eventsourced.mutable.publisher;

import com.damdamdeo.eventsourced.encryption.api.CryptoService;
import com.damdamdeo.eventsourced.encryption.api.UnableToEncryptMissingSecretException;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.fasterxml.jackson.databind.JsonNode;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnsupportedCryptoService implements CryptoService<JsonNode> {

    @Override
    public void encrypt(final AggregateRootId aggregateRootId,
                        final JsonNode parentNode,
                        final String fieldName,
                        final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public void decrypt(final JsonNode parentNode,
                        final String fieldName) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public void recursiveDecrypt(final JsonNode source) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

}
