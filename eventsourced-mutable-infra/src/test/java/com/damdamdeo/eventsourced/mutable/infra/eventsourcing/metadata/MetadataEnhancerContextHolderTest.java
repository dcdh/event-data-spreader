package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.metadata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class MetadataEnhancerContextHolderTest {

    @BeforeEach
    public void setup() {
        MetadataEnhancerContextHolder.cleanupThread();
    }

    @Test
    public void should_thread_be_cleaned_up_when_cleaning_up_thread() {
        // Given
        MetadataEnhancerContextHolder.put("key", "value");

        // When
        MetadataEnhancerContextHolder.cleanupThread();

        // Then
        assertNull(MetadataEnhancerContextHolder.get("key"));
    }

    @Test
    public void should_return_null_when_key_does_not_exists() {
        // Given

        // When
        final Object value = MetadataEnhancerContextHolder.get("nonexistentKey");

        // Then
        assertNull(value);
    }

}
