package com.damdamdeo.eventsourced.mutable.publisher;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KafkaConnectorStatusTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(KafkaConnectorStatus.class).verify();
    }

    @ParameterizedTest
    @ValueSource(strings = {"FAILED", "PAUSED"})
    public void should_is_running_return_false_when_state_is_not_running(final String givenState) {
        // Given
        final KafkaConnectorStatus kafkaConnectorStatus = new KafkaConnectorStatus("name",
                new Connector(givenState, "workerId"));

        // When && Then
        assertFalse(kafkaConnectorStatus.isRunning());
    }

    @Test
    public void should_is_running_return_true_when_state_is_running() {
        // Given
        final KafkaConnectorStatus kafkaConnectorStatus = new KafkaConnectorStatus("name",
                new Connector("RUNNING", "workerId"));

        // When && Then
        assertTrue(kafkaConnectorStatus.isRunning());
    }

}
