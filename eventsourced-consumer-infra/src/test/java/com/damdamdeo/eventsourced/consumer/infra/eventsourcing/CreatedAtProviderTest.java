package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class CreatedAtProviderTest {

    @Inject
    CreatedAtProvider createdAtProvider;

    @Test
    public void should_generate_at_call() {
        // Given
        final LocalDateTime startInclusive = LocalDateTime.now();

        // When
        final LocalDateTime createdAt = createdAtProvider.createdAt();
        final LocalDateTime endInclusive = LocalDateTime.now();

        // Then
        assertTrue((createdAt.isAfter(startInclusive) || createdAt.equals(startInclusive))
                && (createdAt.isBefore(endInclusive) || createdAt.equals(endInclusive)));
    }

}
