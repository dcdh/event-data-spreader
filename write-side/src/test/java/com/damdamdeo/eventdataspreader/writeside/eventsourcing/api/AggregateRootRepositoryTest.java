package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.AggregateRootEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AggregateRootRepositoryTest {

    private AbstractAggregateRootRepository abstractAggregateRootRepository;
    private AggregateRoot aggregateRoot;
    private EventRepository eventRepository;
    private EntityManager entityManager;
    private AggregateRootSerializer aggregateRootSerializer;

    private static final class TestAbstractAggregateRootRepository extends AbstractAggregateRootRepository {

        private final AggregateRoot aggregateRoot;
        private final EventRepository eventRepository;
        private final EntityManager entityManager;
        private final AggregateRootSerializer aggregateRootSerializer;

        public TestAbstractAggregateRootRepository(final AggregateRoot aggregateRoot,
                                                   final EventRepository eventRepository,
                                                   final EntityManager entityManager,
                                                   final AggregateRootSerializer aggregateRootSerializer) {
            this.aggregateRoot = aggregateRoot;
            this.eventRepository = eventRepository;
            this.entityManager = entityManager;
            this.aggregateRootSerializer = aggregateRootSerializer;
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
        protected EntityManager entityManager() {
            return entityManager;
        }

        @Override
        protected AggregateRootSerializer aggregateRootSerializer() {
            return aggregateRootSerializer;
        }
    }
    @BeforeEach
    public void setup() {
        aggregateRoot = mock(AggregateRoot.class);
        eventRepository = mock(EventRepository.class);
        entityManager = mock(EntityManager.class);
        aggregateRootSerializer = mock(AggregateRootSerializer.class);

        abstractAggregateRootRepository = spy(new TestAbstractAggregateRootRepository(aggregateRoot, eventRepository, entityManager, aggregateRootSerializer));
    }

    @Test
    public void should_save_events() {
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
    public void should_purge_events_after_save() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);

        // When
        abstractAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(aggregateRoot).deleteUnsavedEvents();
    }

    @Test
    public void should_save_aggregate() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);

        // When
        abstractAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(entityManager).merge(ArgumentMatchers.any(AggregateRootEntity.class));
    }

    @Test
    public void should_load_aggregateRoot() {
        // Given
        final List<Event> events = Collections.singletonList(mock(Event.class));
        doReturn(events).when(eventRepository).load(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());

        // When
        final AggregateRoot aggregateRootLoaded = abstractAggregateRootRepository.load("aggregateRootId");

        // Then
        assertEquals(aggregateRoot, aggregateRootLoaded);
        verify(aggregateRoot).loadFromHistory(events);
        verify(abstractAggregateRootRepository).createNewInstance();
        verify(eventRepository).load(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());
    }

    @Test
    public void should_throw_exception_when_no_events_are_presents() {
        // Given
        doReturn(Collections.emptyList()).when(eventRepository).load(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());

        // When && Then
        Assertions.assertThrows(UnknownAggregateRootException.class, () -> {
            abstractAggregateRootRepository.load("aggregateRootId");
        });

        verify(aggregateRoot, never()).loadFromHistory(ArgumentMatchers.anyList());
        verify(abstractAggregateRootRepository).createNewInstance();
        verify(eventRepository).load(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());
    }

}
