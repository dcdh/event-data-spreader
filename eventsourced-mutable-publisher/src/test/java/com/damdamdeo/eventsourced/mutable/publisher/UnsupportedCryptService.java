package com.damdamdeo.eventsourced.mutable.publisher;

import com.damdamdeo.eventsourced.encryption.api.CryptService;
import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.UnableToEncryptMissingSecretException;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.fasterxml.jackson.databind.JsonNode;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnsupportedCryptService implements CryptService<JsonNode> {

    @Override
    public void encrypt(final AggregateRootId aggregateRootId,
                        final JsonNode parentNode,
                        final String fieldName,
                        final Encryption encryption) throws UnableToEncryptMissingSecretException {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public void decrypt(final JsonNode parentNode,
                        final String fieldName,
                        final Encryption encryption) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public void recursiveDecrypt(final JsonNode source, final Encryption encryption) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

}
