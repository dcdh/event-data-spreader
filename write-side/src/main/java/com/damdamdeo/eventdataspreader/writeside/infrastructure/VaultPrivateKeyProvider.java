package com.damdamdeo.eventdataspreader.writeside.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.PrivateKeyProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VaultPrivateKeyProvider implements PrivateKeyProvider {

    @ConfigProperty(name = "private-key")
    String privateKey;

    @Override
    public String getPrivateKey() {
        return privateKey;
    }

}