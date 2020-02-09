package com.damdamdeo.eventdataspreader.queryside.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.PrivateKeyProvider;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class VaultPrivateKeyProviderTest {

    @Inject
    PrivateKeyProvider privateKeyProvider;

    @Test
    public void should_load_secret_from_vault() {
        assertEquals("123456", privateKeyProvider.getPrivateKey());
    }

}
