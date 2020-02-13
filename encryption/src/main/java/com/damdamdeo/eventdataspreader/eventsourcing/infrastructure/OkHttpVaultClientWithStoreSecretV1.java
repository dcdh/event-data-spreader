package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import io.quarkus.vault.runtime.client.OkHttpVaultClient;
import io.quarkus.vault.runtime.client.VaultClientException;
import io.quarkus.vault.runtime.config.VaultRuntimeConfig;

import java.util.Map;

public class OkHttpVaultClientWithStoreSecretV1 extends OkHttpVaultClient {

    public OkHttpVaultClientWithStoreSecretV1(final VaultRuntimeConfig serverConfig) {
        super(serverConfig);
    }

    public void storeSecretV1(final String token,
                              final String secretEnginePath,
                              final String path,
                              final Map<String, String> secret) {
        try {
            post(secretEnginePath + "/" + path, token, secret, Void.class);
        } catch (final VaultClientException e) {
            // 204 is the expected response. I made it fails (by checking on 200) to not fall du to deserialized empty json
            // better implementation proposal here https://github.com/quarkusio/quarkus/issues/7155
            if (e.getStatus() != 204) {
                throw e;
            }
        }
    }

}
