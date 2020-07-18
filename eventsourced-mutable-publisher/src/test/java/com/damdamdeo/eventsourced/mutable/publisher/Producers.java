package com.damdamdeo.eventsourced.mutable.publisher;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventMetadataMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventPayloadMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootMaterializedStateMixInSubtypeDiscovery;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Collections;

public class Producers {

    @Produces
    @ApplicationScoped
    public JacksonAggregateRootEventMetadataMixInSubtypeDiscovery jacksonAggregateRootEventMetadataMixInSubtypeDiscovery() {
        return () -> Collections.emptyList();
    }

    @Produces
    @ApplicationScoped
    public JacksonAggregateRootEventPayloadMixInSubtypeDiscovery jacksonAggregateRootEventPayloadMixInSubtypeDiscovery() {
        return () -> Collections.emptyList();
    }

    @Produces
    @ApplicationScoped
    public JacksonAggregateRootMaterializedStateMixInSubtypeDiscovery jacksonAggregateRootMaterializedStateMixInSubtypeDiscovery() {
        return () -> Collections.emptyList();
    }

    @Produces
    @ApplicationScoped
    public Encryption unsupportedEncryption() {
        return new Encryption() {
            @Override
            public String generateNewSecret() {
                throw new UnsupportedOperationException("Must be mocked !");
            }

            @Override
            public String encrypt(String strToEncrypt, String secret) {
                throw new UnsupportedOperationException("Must be mocked !");
            }

            @Override
            public String decrypt(String strToDecrypt, String secret) {
                throw new UnsupportedOperationException("Must be mocked !");
            }
        };
    }

    @Produces
    @ApplicationScoped
    public SecretStore unsupportedSecretStore() {
        return new SecretStore() {
            @Override
            public Secret store(String aggregateRootType, String aggregateRootId, String secret) {
                throw new UnsupportedOperationException("Must be mocked !");
            }

            @Override
            public Secret read(String aggregateRootType, String aggregateRootId) {
                throw new UnsupportedOperationException("Must be mocked !");
            }
        };
    }
}
