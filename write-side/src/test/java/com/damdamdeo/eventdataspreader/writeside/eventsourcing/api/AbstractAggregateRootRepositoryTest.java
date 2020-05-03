package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.event.api.EventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.AggregateRootEntity;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import javax.persistence.EntityManager;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AbstractAggregateRootRepositoryTest {

    private final class TestAggregateRoot extends AggregateRoot {}

    private final class TestAbstractAggregateRootRepository extends AbstractAggregateRootRepository<TestAggregateRoot> {

        private final TestAggregateRoot testAggregateRoot;
        private final EventRepository eventRepository;
        private final EntityManager entityManager;
        private final AggregateRootSerializer aggregateRootSerializer;

        public TestAbstractAggregateRootRepository(final TestAggregateRoot testAggregateRoot,
                                                   final EventRepository eventRepository,
                                                   final EntityManager entityManager,
                                                   final AggregateRootSerializer aggregateRootSerializer) {
            this.testAggregateRoot = testAggregateRoot;
            this.eventRepository = eventRepository;
            this.entityManager = entityManager;
            this.aggregateRootSerializer = aggregateRootSerializer;
        }

        @Override
        protected TestAggregateRoot createNewInstance() {
            return testAggregateRoot;
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

    @Test
    public void should_fail_fast_when_aggregateRoot_is_null() {
        // Given
        final TestAbstractAggregateRootRepository testAbstractAggregateRootRepository = new TestAbstractAggregateRootRepository(
                mock(TestAggregateRoot.class), mock(EventRepository.class), mock(EntityManager.class), mock(AggregateRootSerializer.class));

        // When && Then
        assertThrows(NullPointerException.class, () -> testAbstractAggregateRootRepository.save(null));
    }

    @Test
    public void should_save_unsaved_event_next_delete_them() {
        // Given
        final TestAggregateRoot testAggregateRoot = spy(new TestAggregateRoot());
        final EventRepository eventRepository = mock(EventRepository.class);
        final EntityManager entityManager = mock(EntityManager.class);
        final AggregateRootSerializer aggregateRootSerializer = mock(AggregateRootSerializer.class);
        final TestAbstractAggregateRootRepository testAbstractAggregateRootRepository = new TestAbstractAggregateRootRepository(
                testAggregateRoot, eventRepository, entityManager, aggregateRootSerializer);
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventPayload.aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventPayload.eventName()).thenReturn("eventName");
        when(aggregateRootEventPayload.aggregateRootType()).thenReturn("aggregateRootType");
        final Event eventToApply = testAggregateRoot.apply(aggregateRootEventPayload, mock(EventMetadata.class));
        final InOrder inOrder = inOrder(testAggregateRoot);

        // When
        testAbstractAggregateRootRepository.save(testAggregateRoot);

        // Then
        verify(eventRepository).save(asList(eventToApply));
        inOrder.verify(testAggregateRoot).unsavedEvents();
        inOrder.verify(testAggregateRoot).deleteUnsavedEvents();
        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootId();
        verify(aggregateRootEventPayload, atLeastOnce()).eventName();
        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootType();
    }

    @Test
    public void should_save_aggregate_root_projection() {
        // Given
        final TestAggregateRoot testAggregateRoot = spy(new TestAggregateRoot());
        final EventRepository eventRepository = mock(EventRepository.class);
        final EntityManager entityManager = mock(EntityManager.class);
        final AggregateRootSerializer aggregateRootSerializer = mock(AggregateRootSerializer.class);
        final TestAbstractAggregateRootRepository testAbstractAggregateRootRepository = new TestAbstractAggregateRootRepository(
                testAggregateRoot, eventRepository, entityManager, aggregateRootSerializer);
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventPayload.aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventPayload.eventName()).thenReturn("eventName");
        when(aggregateRootEventPayload.aggregateRootType()).thenReturn("aggregateRootType");
        testAggregateRoot.apply(aggregateRootEventPayload, mock(EventMetadata.class));

        // When
        testAbstractAggregateRootRepository.save(testAggregateRoot);
// TODO revoir tests !
        // Then
        verify(entityManager).merge(any(AggregateRootEntity.class));
    }

}
