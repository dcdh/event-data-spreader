package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.SecretException;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SecretStore;
import io.quarkus.vault.runtime.VaultAuthManager;
import io.quarkus.vault.runtime.VaultManager;
import io.quarkus.vault.runtime.client.VaultClientException;
import io.quarkus.vault.runtime.config.VaultRuntimeConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.Optional;

@ApplicationScoped
public class VaultSecretStore implements SecretStore {

    private VaultRuntimeConfig vaultRuntimeConfig;
    private VaultAuthManager vaultAuthManager;
    private OkHttpVaultClientWithStoreSecretV1 vaultClient;

    private static final String SECRET = "secret";

    @PostConstruct
    public void onInit() {
        this.vaultRuntimeConfig = VaultManager.getInstance().getServerConfig();
        this.vaultClient = new OkHttpVaultClientWithStoreSecretV1(vaultRuntimeConfig);
        this.vaultAuthManager = new VaultAuthManager(this.vaultClient, vaultRuntimeConfig);
    }

    @Override
    public void store(final String path, final String secret) {
        final String clientToken = vaultAuthManager.getClientToken();
        final String mount = vaultRuntimeConfig.kvSecretEngineMountPath;

        this.vaultClient.storeSecretV1(clientToken, mount, path, Collections.singletonMap(SECRET, secret));
    }

    @Override
    public Optional<String> read(final String path) {
        final String clientToken = vaultAuthManager.getClientToken();
        final String mount = vaultRuntimeConfig.kvSecretEngineMountPath;
        try {
            return Optional.ofNullable(vaultClient.getSecretV1(clientToken, mount, path).data.get(SECRET));
        } catch (final VaultClientException e) {
            if (e.getStatus() != 404) {
                throw new SecretException(e);
            }
            return Optional.empty();
        }
    }

}
