package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.event.api.EventMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.*;
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
    public void should_save_unsaved_event_next_delete_them() throws Exception {
        // Given
        final TestAggregateRoot testAggregateRoot = spy(new TestAggregateRoot());
        final EventRepository eventRepository = mock(EventRepository.class);
        final AggregateRootProjectionRepository aggregateRootProjectionRepository = mock(AggregateRootProjectionRepository.class);
        final TestAbstractAggregateRootRepository testAbstractAggregateRootRepository = new TestAbstractAggregateRootRepository(
                testAggregateRoot, eventRepository, aggregateRootProjectionRepository);
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventPayload.aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventPayload.eventName()).thenReturn("eventName");
        when(aggregateRootEventPayload.aggregateRootType()).thenReturn("aggregateRootType");
        testAggregateRoot.apply(aggregateRootEventPayload, mock(EventMetadata.class));
        final InOrder inOrder = inOrder(testAggregateRoot);

        // When
        testAbstractAggregateRootRepository.save(testAggregateRoot);

        // Then
        verify(eventRepository).save(anyList());
        inOrder.verify(testAggregateRoot).unsavedEvents();
        inOrder.verify(testAggregateRoot).deleteUnsavedEvents();
        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootId();
        verify(aggregateRootEventPayload, atLeastOnce()).eventName();
        verify(aggregateRootEventPayload, atLeastOnce()).aggregateRootType();
    }

    @Test
    public void should_save_aggregate_root_projection() throws Exception {
        // Given
        final TestAggregateRoot testAggregateRoot = spy(new TestAggregateRoot());
        final EventRepository eventRepository = mock(EventRepository.class);
        final AggregateRootProjectionRepository aggregateRootProjectionRepository = mock(AggregateRootProjectionRepository.class);
        final TestAbstractAggregateRootRepository testAbstractAggregateRootRepository = new TestAbstractAggregateRootRepository(
                testAggregateRoot, eventRepository, aggregateRootProjectionRepository);
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventPayload.aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventPayload.eventName()).thenReturn("eventName");
        when(aggregateRootEventPayload.aggregateRootType()).thenReturn("aggregateRootType");
        testAggregateRoot.apply(aggregateRootEventPayload, mock(EventMetadata.class));

        // When
        testAbstractAggregateRootRepository.save(testAggregateRoot);
// TODO revoir tests !
        // Then
        verify(aggregateRootProjectionRepository).merge(testAggregateRoot);
    }

}
