package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AggregateRootRepositoryTest {

    private DefaultAggregateRootRepository defaultAggregateRootRepository;
    private EventRepository eventRepository;
    private AggregateRootProjectionRepository aggregateRootProjectionRepository;

    public final class TestAggregateRoot extends AggregateRoot {

        public TestAggregateRoot() {}

    }

    @BeforeEach
    public void setup() {
        eventRepository = mock(EventRepository.class);
        aggregateRootProjectionRepository = mock(AggregateRootProjectionRepository.class);

        defaultAggregateRootRepository = spy(new DefaultAggregateRootRepository(eventRepository, aggregateRootProjectionRepository));
    }

    @Test
    public void should_save_events() throws Exception {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);
        final List<Event> unsavedEvents = Collections.singletonList(mock(Event.class));
        doReturn(unsavedEvents).when(aggregateRoot).unsavedEvents();

        // When
        final AggregateRoot aggregateRootSaved = defaultAggregateRootRepository.save(aggregateRoot);

        // Then
        assertEquals(aggregateRoot, aggregateRootSaved);
        verify(eventRepository).save(unsavedEvents);
        verify(aggregateRoot).unsavedEvents();
    }

    @Test
    public void should_purge_events_after_save() throws Exception {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);

        // When
        defaultAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(aggregateRoot).deleteUnsavedEvents();
    }

    @Test
    public void should_save_aggregate() throws Exception {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);

        // When
        defaultAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(aggregateRootProjectionRepository).merge(aggregateRoot);
    }

    @Test
    public void should_load_aggregateRoot() {
        // Given
        final List<Event> events = Collections.singletonList(mock(Event.class));
        doReturn(events).when(eventRepository).loadOrderByCreationDateASC(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());
        final TestAggregateRoot aggregateRoot = mock(TestAggregateRoot.class);
        doReturn(aggregateRoot).when(defaultAggregateRootRepository).createNewInstance(TestAggregateRoot.class);

        // When
        final TestAggregateRoot aggregateRootLoaded = defaultAggregateRootRepository.load("aggregateRootId", TestAggregateRoot.class);

        // Then
        assertEquals(aggregateRoot, aggregateRootLoaded);
        verify(aggregateRoot).loadFromHistory(events);
        verify(eventRepository).loadOrderByCreationDateASC(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());
        verify(defaultAggregateRootRepository).createNewInstance(TestAggregateRoot.class);
    }

    @Test
    public void should_throw_exception_when_no_events_are_presents() {
        // Given
        doReturn(Collections.emptyList()).when(eventRepository).loadOrderByCreationDateASC(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());
        final TestAggregateRoot aggregateRoot = mock(TestAggregateRoot.class);
        doReturn(aggregateRoot).when(defaultAggregateRootRepository).createNewInstance(TestAggregateRoot.class);

        // When && Then
        Assertions.assertThrows(UnknownAggregateRootException.class, () -> {
            defaultAggregateRootRepository.load("aggregateRootId", TestAggregateRoot.class);
        });

        verify(aggregateRoot, never()).loadFromHistory(ArgumentMatchers.anyList());
        verify(defaultAggregateRootRepository).createNewInstance(TestAggregateRoot.class);
        verify(eventRepository).loadOrderByCreationDateASC(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());
    }

}
