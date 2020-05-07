package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.event.api.EventMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DefaultAggregateRootRepositoryTest {

    private final class TestAggregateRoot extends AggregateRoot {}

    @Test
    public void should_fail_fast_when_aggregateRoot_is_null() {
        // Given
        final DefaultAggregateRootRepository defaultAggregateRootRepository = new DefaultAggregateRootRepository(
                mock(EventRepository.class), mock(AggregateRootProjectionRepository.class));

        // When && Then
        assertThrows(NullPointerException.class, () -> defaultAggregateRootRepository.save(null));
    }

    @Test
    public void should_save_unsaved_event_next_delete_them() throws Exception {
        // Given
        final TestAggregateRoot testAggregateRoot = spy(new TestAggregateRoot());
        final EventRepository eventRepository = mock(EventRepository.class);
        final AggregateRootProjectionRepository aggregateRootProjectionRepository = mock(AggregateRootProjectionRepository.class);
        final DefaultAggregateRootRepository defaultAggregateRootRepository = new DefaultAggregateRootRepository(
                eventRepository, aggregateRootProjectionRepository);
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventPayload.aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventPayload.eventName()).thenReturn("eventName");
        when(aggregateRootEventPayload.aggregateRootType()).thenReturn("aggregateRootType");
        testAggregateRoot.apply(aggregateRootEventPayload, mock(EventMetadata.class));
        final InOrder inOrder = inOrder(testAggregateRoot);

        // When
        defaultAggregateRootRepository.save(testAggregateRoot);

        // Then
        verify(eventRepository).save(anyList());
        inOrder.verify(testAggregateRoot).unsavedEvents();
        inOrder.verify(testAggregateRoot).deleteUnsavedEvents();
        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootId();
        verify(aggregateRootEventPayload, atLeastOnce()).eventName();
        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootType();
    }

    @Test
    public void should_save_aggregate_root_projection() throws Exception {
        // Given
        final TestAggregateRoot testAggregateRoot = spy(new TestAggregateRoot());
        final EventRepository eventRepository = mock(EventRepository.class);
        final AggregateRootProjectionRepository aggregateRootProjectionRepository = mock(AggregateRootProjectionRepository.class);
        final DefaultAggregateRootRepository defaultAggregateRootRepository = new DefaultAggregateRootRepository(
                eventRepository, aggregateRootProjectionRepository);
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventPayload.aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventPayload.eventName()).thenReturn("eventName");
        when(aggregateRootEventPayload.aggregateRootType()).thenReturn("aggregateRootType");
        testAggregateRoot.apply(aggregateRootEventPayload, mock(EventMetadata.class));

        // When
        defaultAggregateRootRepository.save(testAggregateRoot);
// TODO revoir tests !
        // Then
        verify(aggregateRootProjectionRepository).merge(testAggregateRoot);
    }

}
