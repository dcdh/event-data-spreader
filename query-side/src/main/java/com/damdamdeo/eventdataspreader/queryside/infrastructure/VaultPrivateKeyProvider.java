package com.damdamdeo.eventdataspreader.queryside.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.PrivateKeyProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@Deprecated
@ApplicationScoped
public class VaultPrivateKeyProvider implements PrivateKeyProvider {

    @ConfigProperty(name = "private-key")
    String privateKey;

    @Override
    public String getPrivateKey() {
        return privateKey;
    }

}
