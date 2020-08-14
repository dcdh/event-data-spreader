package com.damdamdeo.eventsourced.mutable.publisher;

import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnsupportedSecretStore implements SecretStore {

    @Override
    public Secret store(final AggregateRootId aggregateRootId, final String secret) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public Secret read(final AggregateRootId aggregateRootId) {
        throw new UnsupportedOperationException("Must be mocked !");
    }
}
