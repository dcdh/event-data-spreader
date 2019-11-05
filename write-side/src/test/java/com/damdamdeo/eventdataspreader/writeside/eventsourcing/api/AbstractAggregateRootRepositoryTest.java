package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AbstractAggregateRootRepositoryTest {

    private final class TestAggregateRoot extends AggregateRoot {}

    private final class TestAbstractAggregateRootRepository extends AbstractAggregateRootRepository<TestAggregateRoot> {

        private final TestAggregateRoot testAggregateRoot;
        private final EventRepository eventRepository;
        private final AggregateRootProjectionRepository aggregateRootProjectionRepository;

        public TestAbstractAggregateRootRepository(final TestAggregateRoot testAggregateRoot,
                                                   final EventRepository eventRepository,
                                                   final AggregateRootProjectionRepository aggregateRootProjectionRepository) {
            this.testAggregateRoot = testAggregateRoot;
            this.eventRepository = eventRepository;
            this.aggregateRootProjectionRepository = aggregateRootProjectionRepository;
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
        protected AggregateRootProjectionRepository aggregateRootProjectionRepository() {
            return aggregateRootProjectionRepository;
        }

    }

    @Test
    public void should_fail_fast_when_aggregateRoot_is_null() {
        // Given
        final TestAbstractAggregateRootRepository testAbstractAggregateRootRepository = new TestAbstractAggregateRootRepository(
                mock(TestAggregateRoot.class), mock(EventRepository.class), mock(AggregateRootProjectionRepository.class));

        // When && Then
        assertThrows(NullPointerException.class, () -> testAbstractAggregateRootRepository.save(null));
    }

    @Test
    public void should_save_unsaved_event_next_delete_them() {
        // Given
        final TestAggregateRoot testAggregateRoot = spy(new TestAggregateRoot());
        final EventRepository eventRepository = mock(EventRepository.class);
        final AggregateRootProjectionRepository aggregateRootProjectionRepository = mock(AggregateRootProjectionRepository.class);
        final TestAbstractAggregateRootRepository testAbstractAggregateRootRepository = new TestAbstractAggregateRootRepository(
                testAggregateRoot, eventRepository, aggregateRootProjectionRepository);
        final EventPayload eventPayload = mock(EventPayload.class, RETURNS_DEEP_STUBS);
        when(eventPayload.eventPayloadIdentifier().aggregateRootId()).thenReturn("aggregateRootId");
        when(eventPayload.eventPayloadIdentifier().eventPayloadType()).thenReturn("eventPayloadType");
        when(eventPayload.eventPayloadIdentifier().aggregateRootType()).thenReturn("aggregateRootType");
        final Event eventToApply = testAggregateRoot.apply(eventPayload, mock(EventMetadata.class));
        final InOrder inOrder = inOrder(testAggregateRoot);

        // When
        testAbstractAggregateRootRepository.save(testAggregateRoot);

        // Then
        verify(eventRepository).save(Collections.singletonList(eventToApply));
        inOrder.verify(testAggregateRoot).unsavedEvents();
        inOrder.verify(testAggregateRoot).deleteUnsavedEvents();
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).aggregateRootId();
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).aggregateRootType();
        verify(eventPayload.eventPayloadIdentifier(), atLeastOnce()).eventPayloadType();
    }

    @Test
    public void should_save_aggregate_root_projection() {
        // Given
        final TestAggregateRoot testAggregateRoot = spy(new TestAggregateRoot());
        final EventRepository eventRepository = mock(EventRepository.class);
        final AggregateRootProjectionRepository aggregateRootProjectionRepository = mock(AggregateRootProjectionRepository.class);
        final TestAbstractAggregateRootRepository testAbstractAggregateRootRepository = new TestAbstractAggregateRootRepository(
                testAggregateRoot, eventRepository, aggregateRootProjectionRepository);
        final EventPayload eventPayload = mock(EventPayload.class, RETURNS_DEEP_STUBS);
        when(eventPayload.eventPayloadIdentifier().aggregateRootId()).thenReturn("aggregateRootId");
        when(eventPayload.eventPayloadIdentifier().eventPayloadType()).thenReturn("eventPayloadType");
        when(eventPayload.eventPayloadIdentifier().aggregateRootType()).thenReturn("aggregateRootType");
        testAggregateRoot.apply(eventPayload, mock(EventMetadata.class));

        // When
        final TestAggregateRoot testAggregateRootSaved = testAbstractAggregateRootRepository.save(testAggregateRoot);

        // Then
        verify(aggregateRootProjectionRepository).save(new AggregateRootProjection(testAggregateRootSaved));
    }

}
