package com.damdamdeo.eventsourced.mutable.publisher;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Paths;

@QuarkusTest
public class DebeziumConnectorConfigurationGeneratorTest {

    @Inject
    DebeziumConnectorConfigurationGenerator debeziumConnectorConfigurationGenerator;

    @Test
    public void should_generate_expected_connector_configuration() throws Exception {
        // Given

        // When
        final String connectorConfiguration = debeziumConnectorConfigurationGenerator.generateConnectorConfiguration();

        // Then
        final String expectedConnectorConfiguration = new String(Files.readAllBytes(Paths.get(getClass().getResource("/expected/debezium.json").toURI())));
        JSONAssert.assertEquals(expectedConnectorConfiguration, connectorConfiguration, true);
    }

}
