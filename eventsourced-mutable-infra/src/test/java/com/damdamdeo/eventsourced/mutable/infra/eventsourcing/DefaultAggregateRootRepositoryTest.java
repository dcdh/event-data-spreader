package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.encryption.api.UnsupportedSecret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class DefaultAggregateRootRepositoryTest {

    @InjectMock
    AggregateRootInstanceCreator aggregateRootInstanceCreator;

    @Inject
    DefaultAggregateRootRepository defaultAggregateRootRepository;

    @InjectMock
    EventRepository eventRepository;

    @InjectMock
    AggregateRootMaterializedStateRepository aggregateRootMaterializedStateRepository;

    @InjectMock
    AggregateRootMaterializedStateSerializer aggregateRootMaterializedStateSerializer;

    @InjectMock
    SecretStore secretStore;

    @InjectMock
    Encryption encryption;

    @Test
    public void should_fail_fast_when_save_null_aggregate_root() {
        // Given
        // When && Then
        assertThrows(NullPointerException.class, () -> defaultAggregateRootRepository.save(null));
    }

    @Test
    public void should_create_secret_on_saving_new_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRoot.version()).thenReturn(0l);
        doReturn("newSecret").when(encryption).generateNewSecret();
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStateSerializer).serialize(any(), any());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(secretStore, times(1)).store("aggregateRootType", "aggregateRootId", "newSecret");
        verify(encryption, times(1)).generateNewSecret();
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(any(), any());
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootType();
        verify(mockAggregateRoot, atLeastOnce()).version();
        verify(aggregateRootInstanceCreator, atLeastOnce()).createNewInstance(any());
    }

    @Test
    public void should_read_secret_on_saving_existing_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRoot.version()).thenReturn(1l);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStateSerializer).serialize(any(), any());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(secretStore, times(1)).read("aggregateRootType", "aggregateRootId");
        verify(encryption, times(0)).generateNewSecret();
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(any(), any());
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootType();
        verify(mockAggregateRoot, atLeastOnce()).version();
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
    }

    @Test
    public void should_load_same_aggregate_root_when_saving_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRoot.version()).thenReturn(1l);
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStateSerializer).serialize(any(), any());
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).read(any(), any());
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());
        final List<AggregateRootEvent> aggregateRootEvents = emptyList();
        doReturn(aggregateRootEvents).when(eventRepository).loadOrderByVersionASC(any(), any(), any());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        eventRepository.loadOrderByVersionASC("aggregateRootId", "aggregateRootType", mockSecret);
        mockAggregateRoot.loadFromHistory(aggregateRootEvents);
        verify(secretStore, times(1)).read(any(), any());
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(any(), any());
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootType();
        verify(mockAggregateRoot, atLeastOnce()).version();
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
    }

    @Test
    public void should_save_aggregate_root_event_with_materialized_state_representation_when_saving_aggregate_root() {
        // Given
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).read(any(), any());

        final AggregateRoot loadedAggregateRootForMaterializedState = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        doReturn(loadedAggregateRootForMaterializedState).when(aggregateRootInstanceCreator).createNewInstance(any());

        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        final AggregateRootEvent mockAggregateRootEvent = mock(AggregateRootEvent.class, RETURNS_DEEP_STUBS);
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mockAggregateRootEvent);
        doReturn(aggregateRootEvents).when(mockAggregateRoot).unsavedEvents();
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRoot.version()).thenReturn(1l);
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStateSerializer).serialize(any(), any());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(loadedAggregateRootForMaterializedState, times(1)).apply(mockAggregateRootEvent.eventPayload(), mockAggregateRootEvent.eventMetaData());
        verify(loadedAggregateRootForMaterializedState, times(1)).deleteUnsavedEvents();
        verify(eventRepository, times(1)).save(mockAggregateRootEvent, loadedAggregateRootForMaterializedState, mockSecret);

        verify(secretStore, atLeastOnce()).read("aggregateRootType", "aggregateRootId");
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootType();
        verify(mockAggregateRoot, atLeastOnce()).version();
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
    }

    @Test
    public void should_purge_events_after_saving_when_saving_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRoot.version()).thenReturn(1l);
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStateSerializer).serialize(any(), any());
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());
        doReturn(mock(Secret.class)).when(secretStore).read(any(), any());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(mockAggregateRoot).deleteUnsavedEvents();
        eventRepository.loadOrderByVersionASC(any(), any(), any());
        verify(secretStore, times(1)).read(any(), any());
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(any(), any());
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootType();
        verify(mockAggregateRoot, atLeastOnce()).version();
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
    }

    @Test
    public void should_save_unencrypted_materialized_state_when_saving_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRoot.version()).thenReturn(1l);
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStateSerializer).serialize(any(), any());
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());
        doReturn(mock(Secret.class)).when(secretStore).read(any(), any());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(new UnsupportedSecret(), mockAggregateRoot);
        aggregateRootMaterializedStateRepository.persist(new DefaultAggregateRootMaterializedState(mockAggregateRoot.aggregateRootId(),
                1l, "serializedAggregateRoot"));
        eventRepository.loadOrderByVersionASC(any(), any(), any());
        verify(secretStore, times(1)).read(any(), any());
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootType();
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
    }

    // Loading aggregate root

    @Test
    public void should_read_secret_from_the_secret_store_when_loading_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any(), any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass());

        // Then
        verify(secretStore, times(1)).read(mockAggregateRoot.getClass().getSimpleName(), "aggregateRootId");
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any(), any());
    }

    @Test
    public void should_load_aggregate_root_events_order_by_version_asc_when_loading_aggregate_root() {
        // Given
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).read(any(), any());
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any(), any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass());

        // Then
        verify(eventRepository, times(1)).loadOrderByVersionASC("aggregateRootId", mockAggregateRoot.getClass().getSimpleName(), mockSecret);
        verify(secretStore, times(1)).read(any(), any());
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
    }

    @Test
    public void should_apply_loaded_events_on_aggregate_root() {
        // Given
        doReturn(mock(Secret.class)).when(secretStore).read(any(), any());
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mock(AggregateRootEvent.class));
        doReturn(aggregateRootEvents).when(eventRepository).loadOrderByVersionASC(any(), any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass());

        // Then
        verify(mockAggregateRoot, times(1)).loadFromHistory(aggregateRootEvents);
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any(), any());
        verify(secretStore, times(1)).read(any(), any());
    }

    @Test
    public void should_load_aggregate_root_from_events() {
        // Given
        doReturn(mock(Secret.class)).when(secretStore).read(any(), any());
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mock(AggregateRootEvent.class));
        doReturn(aggregateRootEvents).when(eventRepository).loadOrderByVersionASC(any(), any(), any());

        // When
        final AggregateRoot mockAggregateRootLoaded = defaultAggregateRootRepository.load("aggregateRootId",
                mockAggregateRoot.getClass());

        // Then
        assertEquals(mockAggregateRoot, mockAggregateRootLoaded);
        verify(secretStore, times(1)).read(any(), any());
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
        verify(mockAggregateRoot, times(1)).loadFromHistory(aggregateRootEvents);
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), anyString(), any());
    }

    @Test
    public void should_throw_exception_when_loading_an_aggregate_root_without_events() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(Collections.emptyList()).when(eventRepository).loadOrderByVersionASC("aggregateRootId", mockAggregateRoot.getClass().getName(), mock(Secret.class));
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());

        // When && Then
        assertThrows(UnknownAggregateRootException.class, () -> defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass()));

        verify(mockAggregateRoot, never()).loadFromHistory(anyList());
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), anyString(), any());
    }

    // Loading aggregate root by version

    @Test
    public void should_read_secret_from_the_secret_store_when_loading_aggregate_root_by_version() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any(), any(), any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass(), 1l);

        // Then
        verify(secretStore, times(1)).read(mockAggregateRoot.getClass().getSimpleName(), "aggregateRootId");
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any(), any(), any());
    }

    @Test
    public void should_load_aggregate_root_events_order_by_version_asc_when_loading_aggregate_root_by_version() {
        // Given
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).read(any(), any());
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any(), any(), any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass(), 1l);

        // Then
        verify(eventRepository, times(1)).loadOrderByVersionASC("aggregateRootId", mockAggregateRoot.getClass().getSimpleName(), mockSecret, 1l);
        verify(secretStore, times(1)).read(any(), any());
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
    }

    @Test
    public void should_apply_loaded_events_on_aggregate_root_by_version() {
        // Given
        doReturn(mock(Secret.class)).when(secretStore).read(any(), any());
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mock(AggregateRootEvent.class));
        doReturn(aggregateRootEvents).when(eventRepository).loadOrderByVersionASC(any(), any(), any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass(), 1l);

        // Then
        verify(mockAggregateRoot, times(1)).loadFromHistory(aggregateRootEvents);
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any(), any(), any());
        verify(secretStore, times(1)).read(any(), any());
    }

    @Test
    public void should_load_aggregate_root_from_events_by_version() {
        // Given
        doReturn(mock(Secret.class)).when(secretStore).read(any(), any());
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any(), any(), any(), any());

        // When
        final AggregateRoot mockAggregateRootLoaded = defaultAggregateRootRepository.load("aggregateRootId",
                mockAggregateRoot.getClass(), 1l);

        // Then
        assertEquals(mockAggregateRoot, mockAggregateRootLoaded);
        verify(secretStore, times(1)).read(any(), any());
        verify(mockAggregateRoot, times(1)).loadFromHistory(any());
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), anyString(), any(), any());
    }

    @Test
    public void should_throw_exception_when_loading_an_aggregate_root_without_events_by_version() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(Collections.emptyList()).when(eventRepository).loadOrderByVersionASC("aggregateRootId", mockAggregateRoot.getClass().getName(), mock(Secret.class), 1l);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(mockAggregateRoot.getClass());

        // When && Then
        assertThrows(UnknownAggregateRootException.class, () -> defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass(), 1l));

        verify(mockAggregateRoot, never()).loadFromHistory(anyList());
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), anyString(), any(), any());
    }

}
