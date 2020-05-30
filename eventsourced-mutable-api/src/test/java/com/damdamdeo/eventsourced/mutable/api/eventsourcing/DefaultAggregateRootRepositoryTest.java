package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DefaultAggregateRootRepositoryTest {

    private DefaultAggregateRootRepository defaultAggregateRootRepository;
    private EventRepository eventRepository;
    private AggregateRootMaterializedStateRepository aggregateRootMaterializedStateRepository;
    private AggregateRootMaterializedStateSerializer aggregateRootMaterializedStateSerializer;

    public final class TestAggregateRoot extends AggregateRoot {

        public TestAggregateRoot() {}

    }

    @BeforeEach
    public void setup() {
        eventRepository = mock(EventRepository.class);
        aggregateRootMaterializedStateRepository = mock(AggregateRootMaterializedStateRepository.class);
        aggregateRootMaterializedStateSerializer = mock(AggregateRootMaterializedStateSerializer.class);

        defaultAggregateRootRepository = spy(new DefaultAggregateRootRepository(eventRepository,
                aggregateRootMaterializedStateRepository, aggregateRootMaterializedStateSerializer));
    }

    @Test
    public void should_fail_fast_when_save_null_aggregateRoot() {
        // Given
        // When && Then
        assertThrows(NullPointerException.class, () -> defaultAggregateRootRepository.save(null));
    }

    @Test
    public void should_save_events() throws Exception {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);
        doReturn(mock(AggregateRootId.class)).when(aggregateRoot).aggregateRootId();
        doReturn(0l).when(aggregateRoot).version();
        doReturn("{}").when(aggregateRootMaterializedStateSerializer).serialize(aggregateRoot);
        final List<AggregateRootEvent> unsavedAggregateRootEvents = Collections.singletonList(mock(AggregateRootEvent.class));
        doReturn(unsavedAggregateRootEvents).when(aggregateRoot).unsavedEvents();

        // When
        final AggregateRoot aggregateRootSaved = defaultAggregateRootRepository.save(aggregateRoot);

        // Then
        assertEquals(aggregateRoot, aggregateRootSaved);
        verify(eventRepository, times(1)).save(unsavedAggregateRootEvents);
        verify(aggregateRoot, times(1)).unsavedEvents();
        verify(aggregateRoot, times(1)).deleteUnsavedEvents();
        verify(aggregateRoot, times(1)).aggregateRootId();
        verify(aggregateRoot, times(1)).version();
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(ArgumentMatchers.any());
    }

    @Test
    public void should_purge_events_after_save() throws Exception {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);
        doReturn(mock(AggregateRootId.class)).when(aggregateRoot).aggregateRootId();
        doReturn(0l).when(aggregateRoot).version();
        doReturn("{}").when(aggregateRootMaterializedStateSerializer).serialize(aggregateRoot);

        // When
        defaultAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(aggregateRoot, times(1)).deleteUnsavedEvents();
        verify(aggregateRoot, times(1)).aggregateRootId();
        verify(aggregateRoot, times(1)).version();
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(ArgumentMatchers.any());
    }

    @Test
    public void should_save_aggregate() throws Exception {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);
        doReturn(mock(AggregateRootId.class)).when(aggregateRoot).aggregateRootId();
        doReturn(0l).when(aggregateRoot).version();
        doReturn("{}").when(aggregateRootMaterializedStateSerializer).serialize(aggregateRoot);

        // When
        defaultAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(aggregateRoot);
        verify(aggregateRootMaterializedStateRepository, times(1)).persist(ArgumentMatchers.any());
        verify(aggregateRoot, times(1)).aggregateRootId();
        verify(aggregateRoot, times(1)).version();
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(ArgumentMatchers.any());
    }

    @Test
    public void should_load_aggregateRoot() {
        // Given
        final List<AggregateRootEvent> aggregateRootEvents = Collections.singletonList(mock(AggregateRootEvent.class));
        doReturn(aggregateRootEvents).when(eventRepository).loadOrderByVersionASC(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());
        final TestAggregateRoot aggregateRoot = mock(TestAggregateRoot.class);
        doReturn(aggregateRoot).when(defaultAggregateRootRepository).createNewInstance(TestAggregateRoot.class);

        // When
        final TestAggregateRoot aggregateRootLoaded = defaultAggregateRootRepository.load("aggregateRootId", TestAggregateRoot.class);

        // Then
        assertEquals(aggregateRoot, aggregateRootLoaded);
        verify(aggregateRoot).loadFromHistory(aggregateRootEvents);
        verify(eventRepository).loadOrderByVersionASC(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());
        verify(defaultAggregateRootRepository).createNewInstance(TestAggregateRoot.class);
    }

    @Test
    public void should_throw_exception_when_no_events_are_presents() {
        // Given
        doReturn(Collections.emptyList()).when(eventRepository).loadOrderByVersionASC(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());
        final TestAggregateRoot aggregateRoot = mock(TestAggregateRoot.class);
        doReturn(aggregateRoot).when(defaultAggregateRootRepository).createNewInstance(TestAggregateRoot.class);

        // When && Then
        Assertions.assertThrows(UnknownAggregateRootException.class, () -> {
            defaultAggregateRootRepository.load("aggregateRootId", TestAggregateRoot.class);
        });

        verify(aggregateRoot, never()).loadFromHistory(ArgumentMatchers.anyList());
        verify(defaultAggregateRootRepository).createNewInstance(TestAggregateRoot.class);
        verify(eventRepository).loadOrderByVersionASC(ArgumentMatchers.eq("aggregateRootId"), ArgumentMatchers.anyString());
    }

}
