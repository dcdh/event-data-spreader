package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.encryption.api.UnsupportedSecret;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// TODO utiliser le context Quarkus via QuarkusTest !!!
// C'est choquant parce que je suis dans une api ... je devrais deplacer cela dans infra ...
public class DefaultAggregateRootRepositoryTest {

    private DefaultAggregateRootRepository defaultAggregateRootRepository;
    private EventRepository eventRepository;
    private AggregateRootMaterializedStateRepository aggregateRootMaterializedStateRepository;
    private AggregateRootMaterializedStateSerializer aggregateRootMaterializedStateSerializer;
    private SecretStore secretStore;
    private Encryption encryption;

    @BeforeEach
    public void setup() {
        eventRepository = mock(EventRepository.class);
        aggregateRootMaterializedStateRepository = mock(AggregateRootMaterializedStateRepository.class);
        aggregateRootMaterializedStateSerializer = mock(AggregateRootMaterializedStateSerializer.class);
        secretStore = mock(SecretStore.class);
        encryption = mock(Encryption.class);

        defaultAggregateRootRepository = spy(new DefaultAggregateRootRepository(eventRepository,
                aggregateRootMaterializedStateRepository, aggregateRootMaterializedStateSerializer,
                secretStore, encryption));
    }

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
    }

    @Test
    public void should_read_secret_on_saving_existing_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRoot.version()).thenReturn(1l);
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
    }

    @Test
    public void should_save_aggregate_root_event_with_materialized_state_representation_when_saving_aggregate_root() {
        // Given
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).read(any(), any());

        final AggregateRoot loadedAggregateRootForMaterializedState = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        doReturn(loadedAggregateRootForMaterializedState).when(defaultAggregateRootRepository).createNewInstance(any());

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
    }

    @Test
    public void should_purge_events_after_saving_when_saving_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRoot.version()).thenReturn(1l);
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStateSerializer).serialize(any(), any());
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
    }

    @Test
    public void should_save_unencrypted_materialized_state_when_saving_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRoot.version()).thenReturn(1l);
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStateSerializer).serialize(any(), any());
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
    }

    // Loading aggregate root

    @Test
    public void should_read_secret_from_the_secret_store_when_loading_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any(), any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", AggregateRoot.class);

        // Then
        verify(secretStore, times(1)).read(mockAggregateRoot.getClass().getSimpleName(), "aggregateRootId");
        verify(defaultAggregateRootRepository, times(1)).createNewInstance(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any(), any());
    }

    @Test
    public void should_load_aggregate_root_events_order_by_version_asc_when_loading_aggregate_root() {
        // Given
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).read(any(), any());
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any(), any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", AggregateRoot.class);

        // Then
        verify(eventRepository, times(1)).loadOrderByVersionASC("aggregateRootId", mockAggregateRoot.getClass().getSimpleName(), mockSecret);
        verify(secretStore).read(any(), any());
        verify(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);
    }

    @Test
    public void should_apply_loaded_events_on_aggregate_root() {
        // Given
        doReturn(mock(Secret.class)).when(secretStore).read(any(), any());
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mock(AggregateRootEvent.class));
        doReturn(aggregateRootEvents).when(eventRepository).loadOrderByVersionASC(any(), any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", AggregateRoot.class);

        // Then
        verify(mockAggregateRoot, times(1)).loadFromHistory(aggregateRootEvents);
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any(), any());
        verify(secretStore).read(any(), any());
        verify(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);
    }

    @Test
    public void should_load_aggregate_root_from_events() {
        // Given
        doReturn(mock(Secret.class)).when(secretStore).read(any(), any());
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mock(AggregateRootEvent.class));
        doReturn(aggregateRootEvents).when(eventRepository).loadOrderByVersionASC(any(), any(), any());

        // When
        final AggregateRoot mockAggregateRootLoaded = defaultAggregateRootRepository.load("aggregateRootId", AggregateRoot.class);

        // Then
        assertEquals(mockAggregateRoot, mockAggregateRootLoaded);
        verify(secretStore, times(1)).read(any(), any());
        verify(mockAggregateRoot, times(1)).loadFromHistory(aggregateRootEvents);
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), anyString(), any());
        verify(defaultAggregateRootRepository, times(1)).createNewInstance(any());
    }

    @Test
    public void should_throw_exception_when_loading_an_aggregate_root_without_events() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(Collections.emptyList()).when(eventRepository).loadOrderByVersionASC("aggregateRootId", mockAggregateRoot.getClass().getName(), mock(Secret.class));
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);

        // When && Then
        assertThrows(UnknownAggregateRootException.class, () -> defaultAggregateRootRepository.load("aggregateRootId", AggregateRoot.class));

        verify(mockAggregateRoot, never()).loadFromHistory(anyList());
        verify(defaultAggregateRootRepository, times(1)).createNewInstance(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), anyString(), any());
    }

    // Loading aggregate root by version

    @Test
    public void should_read_secret_from_the_secret_store_when_loading_aggregate_root_by_version() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any(), any(), any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", AggregateRoot.class, 1l);

        // Then
        verify(secretStore, times(1)).read(mockAggregateRoot.getClass().getSimpleName(), "aggregateRootId");
        verify(defaultAggregateRootRepository, times(1)).createNewInstance(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any(), any(), any());
    }

    @Test
    public void should_load_aggregate_root_events_order_by_version_asc_when_loading_aggregate_root_by_version() {
        // Given
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).read(any(), any());
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any(), any(), any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", AggregateRoot.class, 1l);

        // Then
        verify(eventRepository, times(1)).loadOrderByVersionASC("aggregateRootId", mockAggregateRoot.getClass().getSimpleName(), mockSecret, 1l);
        verify(secretStore).read(any(), any());
        verify(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);
    }

    @Test
    public void should_apply_loaded_events_on_aggregate_root_by_version() {
        // Given
        doReturn(mock(Secret.class)).when(secretStore).read(any(), any());
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mock(AggregateRootEvent.class));
        doReturn(aggregateRootEvents).when(eventRepository).loadOrderByVersionASC(any(), any(), any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", AggregateRoot.class, 1l);

        // Then
        verify(mockAggregateRoot, times(1)).loadFromHistory(aggregateRootEvents);
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any(), any(), any());
        verify(secretStore).read(any(), any());
        verify(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);
    }

    @Test
    public void should_load_aggregate_root_from_events_by_version() {
        // Given
        doReturn(mock(Secret.class)).when(secretStore).read(any(), any());
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any(), any(), any(), any());

        // When
        final AggregateRoot mockAggregateRootLoaded = defaultAggregateRootRepository.load("aggregateRootId", AggregateRoot.class, 1l);

        // Then
        assertEquals(mockAggregateRoot, mockAggregateRootLoaded);
        verify(secretStore, times(1)).read(any(), any());
        verify(mockAggregateRoot, times(1)).loadFromHistory(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), anyString(), any(), any());
        verify(defaultAggregateRootRepository, times(1)).createNewInstance(any());
    }

    @Test
    public void should_throw_exception_when_loading_an_aggregate_root_without_events_by_version() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(Collections.emptyList()).when(eventRepository).loadOrderByVersionASC("aggregateRootId", mockAggregateRoot.getClass().getName(), mock(Secret.class), 1l);
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);

        // When && Then
        assertThrows(UnknownAggregateRootException.class, () -> defaultAggregateRootRepository.load("aggregateRootId", AggregateRoot.class, 1l));

        verify(mockAggregateRoot, never()).loadFromHistory(anyList());
        verify(defaultAggregateRootRepository, times(1)).createNewInstance(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), anyString(), any(), any());
    }

}
