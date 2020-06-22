package com.damdamdeo.eventsourced.mutable.infra;

import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnsupportedSecretStore implements SecretStore {

    @Override
    public AggregateRootSecret store(final String aggregateRootType, final String aggregateRootId, final String secret) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public AggregateRootSecret read(final String aggregateRootType, final String aggregateRootId) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

}
