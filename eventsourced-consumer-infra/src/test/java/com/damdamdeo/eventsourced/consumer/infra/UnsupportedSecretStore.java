package com.damdamdeo.eventsourced.consumer.infra;

import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.encryption.api.Secret;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnsupportedSecretStore implements SecretStore {

    @Override
    public Secret store(final String aggregateRootType, final String aggregateRootId, final String secret) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public Secret read(final String aggregateRootType, final String aggregateRootId) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

}
