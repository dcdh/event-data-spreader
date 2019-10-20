package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class AggregateRootTest {

    public static class TestAggregateRoot extends AggregateRoot {

        public TestAggregateRoot(final String aggregateRootId) {
            this.aggregateRootId = aggregateRootId;
        }

    }

    @Test
    public void should_applying_add_new_event() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));
        final EventPayload eventPayload = mock(EventPayload.class, RETURNS_DEEP_STUBS);
        final EventMetadata eventMetadata = mock(EventMetadata.class);
        when(eventPayload.eventPayloadIdentifier().aggregateRootId()).thenReturn("0123456789");
        when(eventPayload.eventPayloadIdentifier().aggregateRootType()).thenReturn("aggregateRootType");
        when(eventPayload.eventPayloadIdentifier().eventPayloadType()).thenReturn("eventPayloadType");

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
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).eventPayloadType();
    }

    @Test
    public void should_deleteUnsavedEvents_delete_unsaved_events() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));
        final EventPayload eventPayload = mock(EventPayload.class, RETURNS_DEEP_STUBS);
        final EventMetadata eventMetadata = mock(EventMetadata.class);

        when(eventPayload.eventPayloadIdentifier().aggregateRootId()).thenReturn("0123456789");
        when(eventPayload.eventPayloadIdentifier().aggregateRootType()).thenReturn("aggregateRootType");
        when(eventPayload.eventPayloadIdentifier().eventPayloadType()).thenReturn("eventPayloadType");

        aggregateRoot.apply(eventPayload, eventMetadata);

        // When
        aggregateRoot.deleteUnsavedEvents();

        // Then
        assertEquals(0l, aggregateRoot.unsavedEvents().size());
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).aggregateRootId();
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).aggregateRootType();
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).eventPayloadType();
    }

    @Test
    public void should_load_from_history_apply_given_events() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));
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

}
