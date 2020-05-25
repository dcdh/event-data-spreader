package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AggregateRootTest {

    public static class TestAggregateRoot extends AggregateRoot {

        public TestAggregateRoot(final String aggregateRootId) {
            this.aggregateRootId = aggregateRootId;
        }

        public TestAggregateRoot() {}

    }

    @Test
    public void should_apply_first_event() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot());
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        final AggregateRootEventMetadata aggregateRootEventMetadata = mock(AggregateRootEventMetadata.class);
        when(aggregateRootEventPayload.aggregateRootId()).thenReturn("0123456789");
        when(aggregateRootEventPayload.eventPayloadName()).thenReturn("eventName");
        when(aggregateRootEventPayload.aggregateRootType()).thenReturn("aggregateRootType");

        // When
        aggregateRoot.apply(aggregateRootEventPayload, aggregateRootEventMetadata);

        // Then
        assertEquals(0l, aggregateRoot.version());

        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootId();
        verify(aggregateRootEventPayload, atLeastOnce()).eventPayloadName();
        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootType();
    }

    @Test
    public void should_applying_add_new_event() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        final AggregateRootEventMetadata aggregateRootEventMetadata = mock(AggregateRootEventMetadata.class);
        when(aggregateRootEventPayload.aggregateRootId()).thenReturn("0123456789");
        when(aggregateRootEventPayload.eventPayloadName()).thenReturn("eventName");
        when(aggregateRootEventPayload.aggregateRootType()).thenReturn("aggregateRootType");

        // When
        aggregateRoot.apply(aggregateRootEventPayload, aggregateRootEventMetadata);

        // Then
        assertEquals("0123456789", aggregateRoot.unsavedEvents().get(0).aggregateRootId());
        assertNotNull(aggregateRoot.unsavedEvents().get(0).aggregateRootType());
        assertNotNull(aggregateRoot.unsavedEvents().get(0).eventType());
        assertEquals(0, aggregateRoot.unsavedEvents().get(0).version());
        assertNotNull(aggregateRoot.unsavedEvents().get(0).creationDate());
        assertEquals(aggregateRootEventMetadata, aggregateRoot.unsavedEvents().get(0).eventMetaData());
        assertEquals(aggregateRootEventPayload, aggregateRoot.unsavedEvents().get(0).eventPayload());
        assertEquals(0l, aggregateRoot.version());
        assertEquals(1, aggregateRoot.unsavedEvents().size());

        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootId();
        verify(aggregateRootEventPayload, atLeastOnce()).eventPayloadName();
        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootType();
    }

    @Test
    public void should_apply_fail_fast_if_event_payload_aggregate_root_id_is_null() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot());
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventPayload.aggregateRootId()).thenReturn(null);

        // When && Then
        assertThrows(NullPointerException.class,
                () -> aggregateRoot.apply(aggregateRootEventPayload, mock(AggregateRootEventMetadata.class)),
                "Aggregate root id can't be null"
        );
        verify(aggregateRootEventPayload, times(1)).aggregateRootId();
    }

    @Test
    public void should_apply_fail_fast_if_aggregate_root_id_mismatch() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventPayload.aggregateRootId()).thenReturn("AZERTY");

        // When && Then
        assertThrows(IllegalStateException.class,
                () -> aggregateRoot.apply(aggregateRootEventPayload, mock(AggregateRootEventMetadata.class)),
                "Aggregate root id and event aggregate root id mismatch"
        );
        verify(aggregateRootEventPayload, times(1)).aggregateRootId();
    }

    @Test
    public void should_load_from_history_fail_fast_if_aggregate_root_id_already_defined_so_aggregate_root_already_loaded() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));

        // When && Then
        assertThrows(IllegalStateException.class,
                () -> aggregateRoot.loadFromHistory(Collections.emptyList()),
                "Aggregate Root already loaded from history");
    }

    @Test
    public void should_load_from_history_fail_fast_if_events_ids_are_different() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot());
        final List<AggregateRootEvent> aggregateRootEvents = new ArrayList<>();
        final AggregateRootEvent aggregateRootEvent1 = mock(AggregateRootEvent.class);
        doReturn("0123456789").when(aggregateRootEvent1).aggregateRootId();
        final AggregateRootEvent aggregateRootEvent2 = mock(AggregateRootEvent.class);
        doReturn("azerty").when(aggregateRootEvent2).aggregateRootId();
        aggregateRootEvents.add(aggregateRootEvent1);
        aggregateRootEvents.add(aggregateRootEvent2);

        // When && Then
        assertThrows(IllegalStateException.class,
                () -> aggregateRoot.loadFromHistory(aggregateRootEvents),
                "Aggregate Root ids events mismatch");
    }

    @Test
    public void should_load_from_history_apply_no_event() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot());

        // When
        aggregateRoot.loadFromHistory(Collections.emptyList());

        // Then
        assertTrue(aggregateRoot.version().equals(-1l));
    }

    @Test
    public void should_load_from_history_apply_given_events() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot());
        final AggregateRootEvent aggregateRootEvent = mock(AggregateRootEvent.class);
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class);
        doReturn(0l).when(aggregateRootEvent).version();
        doReturn(aggregateRootEventPayload).when(aggregateRootEvent).eventPayload();
        final List<AggregateRootEvent> aggregateRootEvents = Collections.singletonList(aggregateRootEvent);

        // When
        aggregateRoot.loadFromHistory(aggregateRootEvents);

        // Then
        verify(aggregateRootEvent).version();
        verify(aggregateRootEvent).eventPayload();
        verify(aggregateRootEventPayload).apply(aggregateRoot);
        assertEquals(0l, aggregateRoot.version());
        assertEquals(0l, aggregateRoot.unsavedEvents().size());
    }

    @Test
    public void should_deleteUnsavedEvents_delete_unsaved_events() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        final AggregateRootEventMetadata aggregateRootEventMetadata = mock(AggregateRootEventMetadata.class);

        when(aggregateRootEventPayload.aggregateRootId()).thenReturn("0123456789");
        when(aggregateRootEventPayload.eventPayloadName()).thenReturn("eventName");
        when(aggregateRootEventPayload.aggregateRootType()).thenReturn("aggregateRootType");

        aggregateRoot.apply(aggregateRootEventPayload, aggregateRootEventMetadata);

        // When
        aggregateRoot.deleteUnsavedEvents();

        // Then
        assertEquals(0l, aggregateRoot.unsavedEvents().size());
        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootId();
        verify(aggregateRootEventPayload, atLeastOnce()).eventPayloadName();
        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootType();
    }

}
