package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import org.junit.jupiter.api.Assertions;
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
        final EventPayload eventPayload = mock(EventPayload.class, RETURNS_DEEP_STUBS);
        final EventMetadata eventMetadata = mock(EventMetadata.class);
        when(eventPayload.eventPayloadIdentifier().aggregateRootId()).thenReturn("0123456789");
        when(eventPayload.eventPayloadIdentifier().aggregateRootType()).thenReturn("aggregateRootType");
        when(eventPayload.eventPayloadIdentifier().eventType()).thenReturn("eventType");

        // When
        aggregateRoot.apply(eventPayload, eventMetadata);

        // Then
        assertEquals(0l, aggregateRoot.version());

        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).aggregateRootId();
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).aggregateRootType();
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).eventType();
    }

    @Test
    public void should_applying_add_new_event() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));
        final EventPayload eventPayload = mock(EventPayload.class, RETURNS_DEEP_STUBS);
        final EventMetadata eventMetadata = mock(EventMetadata.class);
        when(eventPayload.eventPayloadIdentifier().aggregateRootId()).thenReturn("0123456789");
        when(eventPayload.eventPayloadIdentifier().aggregateRootType()).thenReturn("aggregateRootType");
        when(eventPayload.eventPayloadIdentifier().eventType()).thenReturn("eventType");

        // When
        aggregateRoot.apply(eventPayload, eventMetadata);

        // Then
        assertEquals("0123456789", aggregateRoot.unsavedEvents().get(0).aggregateRootId());
        assertNotNull(aggregateRoot.unsavedEvents().get(0).aggregateRootType());
        assertNotNull(aggregateRoot.unsavedEvents().get(0).eventType());
        assertEquals(0, aggregateRoot.unsavedEvents().get(0).version());
        assertNotNull(aggregateRoot.unsavedEvents().get(0).creationDate());
        assertEquals(eventMetadata, aggregateRoot.unsavedEvents().get(0).eventMetaData());
        assertEquals(eventPayload, aggregateRoot.unsavedEvents().get(0).eventPayload());
        assertEquals(0l, aggregateRoot.version());
        assertEquals(1, aggregateRoot.unsavedEvents().size());
        assertNotNull(aggregateRoot.unsavedEvents().get(0).eventId());

        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).aggregateRootId();
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).aggregateRootType();
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).eventType();
    }

    @Test
    public void should_apply_fail_fast_if_event_payload_aggregate_root_id_is_null() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot());
        final EventPayload eventPayload = mock(EventPayload.class, RETURNS_DEEP_STUBS);
        when(eventPayload.eventPayloadIdentifier().aggregateRootId()).thenReturn(null);

        // When && Then
        Assertions.assertThrows(NullPointerException.class,
                () -> aggregateRoot.apply(eventPayload, mock(EventMetadata.class)),
                "Aggregate root id can't be null"
        );
        verify(eventPayload.eventPayloadIdentifier(), times(1)).aggregateRootId();
    }

    @Test
    public void should_apply_fail_fast_if_aggregate_root_id_mismatch() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));
        final EventPayload eventPayload = mock(EventPayload.class, RETURNS_DEEP_STUBS);
        when(eventPayload.eventPayloadIdentifier().aggregateRootId()).thenReturn("AZERTY");

        // When && Then
        Assertions.assertThrows(IllegalStateException.class,
                () -> aggregateRoot.apply(eventPayload, mock(EventMetadata.class)),
                "Aggregate root id and event aggregate root id mismatch"
        );
        verify(eventPayload.eventPayloadIdentifier(), times(1)).aggregateRootId();
    }

    @Test
    public void should_load_from_history_fail_fast_if_aggregate_root_id_already_defined_so_aggregate_root_already_loaded() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));

        // When && Then
        Assertions.assertThrows(IllegalStateException.class,
                () -> aggregateRoot.loadFromHistory(Collections.emptyList()),
                "Aggregate Root already loaded from history");
    }

    @Test
    public void should_load_from_history_fail_fast_if_events_ids_are_different() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot());
        final List<Event> events = new ArrayList<>();
        final Event event1 = mock(Event.class);
        doReturn("0123456789").when(event1).aggregateRootId();
        final Event event2 = mock(Event.class);
        doReturn("azerty").when(event2).aggregateRootId();
        events.add(event1);
        events.add(event2);

        // When && Then
        Assertions.assertThrows(IllegalStateException.class,
                () -> aggregateRoot.loadFromHistory(events),
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
        final Event event = mock(Event.class);
        final EventPayload eventPayload = mock(EventPayload.class);
        doReturn(0l).when(event).version();
        doReturn(eventPayload).when(event).eventPayload();
        final List<Event> events = Collections.singletonList(event);

        // When
        aggregateRoot.loadFromHistory(events);

        // Then
        verify(event).version();
        verify(event).eventPayload();
        verify(eventPayload).apply(aggregateRoot);
        assertEquals(0l, aggregateRoot.version());
        assertEquals(0l, aggregateRoot.unsavedEvents().size());
    }

    @Test
    public void should_deleteUnsavedEvents_delete_unsaved_events() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));
        final EventPayload eventPayload = mock(EventPayload.class, RETURNS_DEEP_STUBS);
        final EventMetadata eventMetadata = mock(EventMetadata.class);

        when(eventPayload.eventPayloadIdentifier().aggregateRootId()).thenReturn("0123456789");
        when(eventPayload.eventPayloadIdentifier().aggregateRootType()).thenReturn("aggregateRootType");
        when(eventPayload.eventPayloadIdentifier().eventType()).thenReturn("eventType");

        aggregateRoot.apply(eventPayload, eventMetadata);

        // When
        aggregateRoot.deleteUnsavedEvents();

        // Then
        assertEquals(0l, aggregateRoot.unsavedEvents().size());
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).aggregateRootId();
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).aggregateRootType();
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).eventType();
    }

}
