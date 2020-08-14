package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AggregateRootTest {

    public static class TestAggregateRoot extends AggregateRoot {

        public TestAggregateRoot() {
            super("aggregateRootId");
        }

    }

    @Test
    public void should_apply_first_event() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);

        // When
        aggregateRoot.apply("eventType", aggregateRootEventPayload);

        // Then
        assertEquals(0l, aggregateRoot.version());
    }

    @Test
    public void should_applying_add_new_event() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        final ApiAggregateRootId aggregateRootId = new ApiAggregateRootId(
                "aggregateRootId", "TestAggregateRoot");

        // When
        aggregateRoot.apply("eventType", aggregateRootEventPayload);

        // Then
        assertEquals(aggregateRootId, aggregateRoot.unsavedEvents().get(0).aggregateRootId());
        assertEquals("eventType", aggregateRoot.unsavedEvents().get(0).eventType());
        assertEquals(0, aggregateRoot.unsavedEvents().get(0).version());
        assertNotNull(aggregateRoot.unsavedEvents().get(0).creationDate());
        assertEquals(aggregateRootEventPayload, aggregateRoot.unsavedEvents().get(0).eventPayload());
        assertEquals(0l, aggregateRoot.version());
        assertEquals(1, aggregateRoot.unsavedEvents().size());
    }

    @Test
    public void should_apply_fail_fast_if_event_type_is_null() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();

        // When && Then
        assertThrows(NullPointerException.class,
                () -> aggregateRoot.apply(null, mock(AggregateRootEventPayload.class)),
                "eventType can't be null"
        );
    }

    @Test
    public void should_apply_fail_fast_if_aggregate_root_event_payload_is_null() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();

        // When && Then
        assertThrows(NullPointerException.class,
                () -> aggregateRoot.apply("eventType", null),
                "aggregateRootEventPayload can't be null"
        );
    }

    @Test
    public void should_load_from_history_fail_fast_if_aggregate_root_already_loaded() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);

        aggregateRoot.apply("eventType", aggregateRootEventPayload);
        reset(aggregateRootEventPayload);

        // When && Then
        assertThrows(IllegalStateException.class,
                () -> aggregateRoot.loadFromHistory(Collections.emptyList()),
                "Aggregate Root already loaded from history");
        verify(aggregateRootEventPayload, times(0)).apply(any());
    }

    @Test
    public void should_load_from_history_fail_fast_if_events_ids_are_different() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final List<AggregateRootEvent> aggregateRootEvents = new ArrayList<>();
        final AggregateRootEvent aggregateRootEvent1 = mock(AggregateRootEvent.class);
        doReturn(mock(AggregateRootId.class)).when(aggregateRootEvent1).aggregateRootId();
        final AggregateRootEvent aggregateRootEvent2 = mock(AggregateRootEvent.class);
        doReturn(mock(AggregateRootId.class)).when(aggregateRootEvent2).aggregateRootId();
        aggregateRootEvents.add(aggregateRootEvent1);
        aggregateRootEvents.add(aggregateRootEvent2);

        // When && Then
        assertThrows(IllegalStateException.class,
                () -> aggregateRoot.loadFromHistory(aggregateRootEvents),
                "Aggregate Root ids events mismatch");
        verify(aggregateRootEvent1, times(1)).aggregateRootId();
        verify(aggregateRootEvent2, times(1)).aggregateRootId();
    }

    @Test
    public void should_fail_fast_when_load_from_history_an_event_with_mismatch_event_aggregate_root_id() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEvent aggregateRootEvent = mock(AggregateRootEvent.class, RETURNS_DEEP_STUBS);
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class);
        when(aggregateRootEvent.aggregateRootId().aggregateRootId()).thenReturn("unknownAggregateRootId");
        final List<AggregateRootEvent> aggregateRootEvents = Collections.singletonList(aggregateRootEvent);

        // When && Then
        assertThrows(IllegalStateException.class,
                () -> aggregateRoot.loadFromHistory(aggregateRootEvents),
                "Aggregate root id and event aggregate root id mismatch");
        verify(aggregateRootEventPayload, times(0)).apply(any());

        verify(aggregateRootEvent.aggregateRootId(), atLeast(1)).aggregateRootId();
    }

    @Test
    public void should_fail_fast_when_load_from_history_an_event_with_mismatch_event_aggregate_root_type() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEvent aggregateRootEvent = mock(AggregateRootEvent.class, RETURNS_DEEP_STUBS);
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class);
        when(aggregateRootEvent.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEvent.aggregateRootId().aggregateRootType()).thenReturn("unknownAggregateRoot");
        final List<AggregateRootEvent> aggregateRootEvents = Collections.singletonList(aggregateRootEvent);

        // When && Then
        assertThrows(IllegalStateException.class,
                () -> aggregateRoot.loadFromHistory(aggregateRootEvents),
                "Aggregate root type and event aggregate root type mismatch");
        verify(aggregateRootEventPayload, times(0)).apply(any());

        verify(aggregateRootEvent.aggregateRootId(), atLeast(1)).aggregateRootId();
        verify(aggregateRootEvent.aggregateRootId(), atLeast(1)).aggregateRootType();
    }

    @Test
    public void should_load_from_history_apply_no_event() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();

        // When
        aggregateRoot.loadFromHistory(Collections.emptyList());

        // Then
        assertTrue(aggregateRoot.version().equals(-1l));
    }

    @Test
    public void should_load_from_history_apply_given_events() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEvent aggregateRootEvent = mock(AggregateRootEvent.class, RETURNS_DEEP_STUBS);
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class);
        when(aggregateRootEvent.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEvent.aggregateRootId().aggregateRootType()).thenReturn("TestAggregateRoot");
        doReturn(0l).when(aggregateRootEvent).version();
        doReturn(aggregateRootEventPayload).when(aggregateRootEvent).eventPayload();
        final List<AggregateRootEvent> aggregateRootEvents = Collections.singletonList(aggregateRootEvent);

        // When
        aggregateRoot.loadFromHistory(aggregateRootEvents);

        // Then
        verify(aggregateRootEvent.aggregateRootId(), times(1)).aggregateRootId();
        verify(aggregateRootEvent.aggregateRootId(), times(1)).aggregateRootType();
        verify(aggregateRootEvent, times(1)).version();
        verify(aggregateRootEvent, times(1)).eventPayload();
        verify(aggregateRootEventPayload, times(1)).apply(aggregateRoot);
        assertEquals(0l, aggregateRoot.version());
        assertEquals(0l, aggregateRoot.unsavedEvents().size());
    }

    @Test
    public void should_deleteUnsavedEvents_delete_unsaved_events() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        aggregateRoot.apply("eventType", aggregateRootEventPayload);

        // When
        aggregateRoot.deleteUnsavedEvents();

        // Then
        assertEquals(0l, aggregateRoot.unsavedEvents().size());
    }

}
