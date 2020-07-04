package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadata;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayload;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AggregateRootTest {

    public static class TestAggregateRoot extends AggregateRoot {

        public TestAggregateRoot() {}

    }

    @Test
    public void should_apply_first_event() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        final AggregateRootEventMetadata aggregateRootEventMetadata = mock(AggregateRootEventMetadata.class);

        // When
        aggregateRoot.apply("eventType", aggregateRootEventPayload, aggregateRootEventMetadata);

        // Then
        assertEquals(0l, aggregateRoot.version());

        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootId();
    }

    @Test
    public void should_applying_add_new_event() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        final AggregateRootEventMetadata aggregateRootEventMetadata = mock(AggregateRootEventMetadata.class);
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn(aggregateRootId).when(aggregateRootEventPayload).aggregateRootId();

        // When
        aggregateRoot.apply("eventType", aggregateRootEventPayload, aggregateRootEventMetadata);

        // Then
        assertEquals(aggregateRoot.unsavedEvents().get(0).aggregateRootId(), aggregateRootId);
        assertEquals("eventType", aggregateRoot.unsavedEvents().get(0).eventType());
        assertEquals(0, aggregateRoot.unsavedEvents().get(0).version());
        assertNotNull(aggregateRoot.unsavedEvents().get(0).creationDate());
        assertEquals(aggregateRootEventMetadata, aggregateRoot.unsavedEvents().get(0).eventMetaData());
        assertEquals(aggregateRootEventPayload, aggregateRoot.unsavedEvents().get(0).eventPayload());
        assertEquals(0l, aggregateRoot.version());
        assertEquals(1, aggregateRoot.unsavedEvents().size());

        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootId();
    }

    @Test
    public void should_apply_fail_fast_if_event_type_is_null() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();

        // When && Then
        assertThrows(NullPointerException.class,
                () -> aggregateRoot.apply(null, mock(AggregateRootEventPayload.class), mock(AggregateRootEventMetadata.class)),
                "eventType can't be null"
        );
    }

    @Test
    public void should_apply_fail_fast_if_event_payload_aggregate_root_id_is_null() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventPayload.aggregateRootId()).thenReturn(null);

        // When && Then
        assertThrows(NullPointerException.class,
                () -> aggregateRoot.apply("eventType", aggregateRootEventPayload, mock(AggregateRootEventMetadata.class)),
                "Aggregate root id can't be null"
        );
        verify(aggregateRootEventPayload, times(1)).aggregateRootId();
    }

    @Test
    public void should_apply_fail_fast_if_aggregate_root_id_mismatch() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEventPayload aggregateRootEventPayload1 = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventPayload1.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId1");
        aggregateRoot.apply("eventType", aggregateRootEventPayload1, mock(AggregateRootEventMetadata.class));
        final AggregateRootEventPayload aggregateRootEventPayload2 = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventPayload2.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId2");

        // When && Then
        assertThrows(IllegalStateException.class,
                () -> aggregateRoot.apply("eventType", aggregateRootEventPayload2, mock(AggregateRootEventMetadata.class)),
                "Aggregate root id and event aggregate root id mismatch"
        );
        verify(aggregateRootEventPayload1.aggregateRootId(), times(1)).aggregateRootId();
        verify(aggregateRootEventPayload2.aggregateRootId(), times(1)).aggregateRootId();
    }

    @Test
    public void should_apply_fail_fast_if_aggregate_root_type_mismatch() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEventPayload aggregateRootEventPayload1 = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventPayload1.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventPayload1.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType1");
        aggregateRoot.apply("eventType", aggregateRootEventPayload1, mock(AggregateRootEventMetadata.class));
        final AggregateRootEventPayload aggregateRootEventPayload2 = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventPayload2.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventPayload2.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType2");

        // When && Then
        assertThrows(IllegalStateException.class,
                () -> aggregateRoot.apply("eventType", aggregateRootEventPayload2, mock(AggregateRootEventMetadata.class)),
                "Aggregate root id and event aggregate root id mismatch"
        );
        verify(aggregateRootEventPayload1.aggregateRootId(), times(1)).aggregateRootId();
        verify(aggregateRootEventPayload1.aggregateRootId(), times(1)).aggregateRootType();
        verify(aggregateRootEventPayload2.aggregateRootId(), times(1)).aggregateRootId();
        verify(aggregateRootEventPayload2.aggregateRootId(), times(1)).aggregateRootType();
    }

    @Test
    public void should_load_from_history_fail_fast_if_aggregate_root_id_already_defined_so_aggregate_root_already_loaded() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        final AggregateRootEventMetadata aggregateRootEventMetadata = mock(AggregateRootEventMetadata.class);

        when(aggregateRootEventPayload.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventPayload.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");

        aggregateRoot.apply("eventType", aggregateRootEventPayload, aggregateRootEventMetadata);

        // When && Then
        assertThrows(IllegalStateException.class,
                () -> aggregateRoot.loadFromHistory(Collections.emptyList()),
                "Aggregate Root already loaded from history");
        verify(aggregateRootEventPayload.aggregateRootId(), times(1)).aggregateRootId();
        verify(aggregateRootEventPayload.aggregateRootId(), times(1)).aggregateRootType();
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
        final AggregateRootEvent aggregateRootEvent = mock(AggregateRootEvent.class);
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class);
        doReturn(mock(AggregateRootId.class)).when(aggregateRootEvent).aggregateRootId();
        doReturn(0l).when(aggregateRootEvent).version();
        doReturn(aggregateRootEventPayload).when(aggregateRootEvent).eventPayload();
        final List<AggregateRootEvent> aggregateRootEvents = Collections.singletonList(aggregateRootEvent);

        // When
        aggregateRoot.loadFromHistory(aggregateRootEvents);

        // Then
        verify(aggregateRootEvent, atLeastOnce()).aggregateRootId();
        verify(aggregateRootEvent).version();
        verify(aggregateRootEvent).eventPayload();
        verify(aggregateRootEventPayload).apply(aggregateRoot);
        assertEquals(0l, aggregateRoot.version());
        assertEquals(0l, aggregateRoot.unsavedEvents().size());
    }

    @Test
    public void should_deleteUnsavedEvents_delete_unsaved_events() {
        // Given
        final AggregateRoot aggregateRoot = new TestAggregateRoot();
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        final AggregateRootEventMetadata aggregateRootEventMetadata = mock(AggregateRootEventMetadata.class);
        aggregateRoot.apply("eventType", aggregateRootEventPayload, aggregateRootEventMetadata);

        // When
        aggregateRoot.deleteUnsavedEvents();

        // Then
        assertEquals(0l, aggregateRoot.unsavedEvents().size());
        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootId();
    }

}
