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

    private AbstractAggregateRootRepository abstractAggregateRootRepository;
    private AggregateRoot aggregateRoot;
    private EventRepository eventRepository;
    private AggregateRootProjectionRepository aggregateRootProjectionRepository;

    private static final class TestAbstractAggregateRootRepository extends AbstractAggregateRootRepository {

        private final AggregateRoot aggregateRoot;
        private final EventRepository eventRepository;
        private final AggregateRootProjectionRepository aggregateRootProjectionRepository;

        public TestAbstractAggregateRootRepository(final AggregateRoot aggregateRoot,
                                                   final EventRepository eventRepository,
                                                   final AggregateRootProjectionRepository aggregateRootProjectionRepository) {
            this.aggregateRoot = aggregateRoot;
            this.eventRepository = eventRepository;
            this.aggregateRootProjectionRepository = aggregateRootProjectionRepository;
        }

        @Override
        protected AggregateRoot createNewInstance() {
            return aggregateRoot;
        }

        @Override
        protected EventRepository eventRepository() {
            return eventRepository;
        }

        @Override
        protected AggregateRootProjectionRepository aggregateRootProjectionRepository() {
            return aggregateRootProjectionRepository;
        }

    }

    @BeforeEach
    public void setup() {
        aggregateRoot = mock(AggregateRoot.class);
        eventRepository = mock(EventRepository.class);
        aggregateRootProjectionRepository = mock(AggregateRootProjectionRepository.class);

        abstractAggregateRootRepository = spy(new TestAbstractAggregateRootRepository(aggregateRoot, eventRepository, aggregateRootProjectionRepository));
    }

    @Test
    public void should_save_events() throws Exception {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);
        final List<Event> unsavedEvents = Collections.singletonList(mock(Event.class));
        doReturn(unsavedEvents).when(aggregateRoot).unsavedEvents();

        // When
        final AggregateRoot aggregateRootSaved = abstractAggregateRootRepository.save(aggregateRoot);

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
        abstractAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(aggregateRoot).deleteUnsavedEvents();
    }

    @Test
    public void should_save_aggregate() throws Exception {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);

        // When
        abstractAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(aggregateRootProjectionRepository).merge(aggregateRoot);
    }

    @Test
    public void should_load_aggregateRoot() {
        // Given
        final List<Event> events = Collections.singletonList(mock(Event.class));
        doReturn(events).when(eventRepository).loadOrderByCreationDateASC(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());

        // When
        final AggregateRoot aggregateRootLoaded = abstractAggregateRootRepository.load("aggregateRootId");

        // Then
        assertEquals(aggregateRoot, aggregateRootLoaded);
        verify(aggregateRoot).loadFromHistory(events);
        verify(abstractAggregateRootRepository).createNewInstance();
        verify(eventRepository).loadOrderByCreationDateASC(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());
    }

    @Test
    public void should_throw_exception_when_no_events_are_presents() {
        // Given
        doReturn(Collections.emptyList()).when(eventRepository).loadOrderByCreationDateASC(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());

        // When && Then
        Assertions.assertThrows(UnknownAggregateRootException.class, () -> {
            abstractAggregateRootRepository.load("aggregateRootId");
        });

        verify(aggregateRoot, never()).loadFromHistory(ArgumentMatchers.anyList());
        verify(abstractAggregateRootRepository).createNewInstance();
        verify(eventRepository).loadOrderByCreationDateASC(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());
    }

}
