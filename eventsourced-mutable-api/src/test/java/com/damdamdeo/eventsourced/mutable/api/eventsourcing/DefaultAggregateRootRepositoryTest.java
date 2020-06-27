package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.encryption.api.UnsupportedSecret;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

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
    public void should_fail_fast_when_save_null_aggregateRoot() {
        // Given
        // When && Then
        assertThrows(NullPointerException.class, () -> defaultAggregateRootRepository.save(null));
    }

    @Test
    public void should_save_events_with_materialized_state() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        final Secret mockSecret = mock(Secret.class);
        doReturn("newSecret").when(encryption).generateNewSecret();
        doReturn("{}").when(aggregateRootMaterializedStateSerializer).serialize(new UnsupportedSecret(), mockAggregateRoot);
        final AggregateRootEvent mockAggregateRootEvent = mock(AggregateRootEvent.class, RETURNS_DEEP_STUBS);
        doReturn(mockSecret).when(secretStore).read(any(), eq("aggregateRootId"));
        doReturn(singletonList(mockAggregateRootEvent)).when(eventRepository)
                .loadOrderByVersionASC(eq("aggregateRootId"), any(), eq(mockSecret));
        final List<AggregateRootEvent> unsavedAggregateRootEvents = singletonList(mockAggregateRootEvent);

        doReturn(unsavedAggregateRootEvents).when(mockAggregateRoot).unsavedEvents();
        doReturn(mockSecret).when(secretStore).store("aggregateRootType", "aggregateRootId", "newSecret");

        final AggregateRoot createdAggregateRoot = mock(AggregateRoot.class);
        doReturn(createdAggregateRoot).when(defaultAggregateRootRepository).createNewInstance(any());

        // When
        final AggregateRoot aggregateRootSaved = defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        assertEquals(mockAggregateRoot, aggregateRootSaved);
        verify(eventRepository, times(1)).save(mockAggregateRootEvent, createdAggregateRoot, mockSecret);

        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(any(), any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any(), any());
        verify(secretStore, times(1)).read(any(), any());
        verify(defaultAggregateRootRepository, times(1)).createNewInstance(any());
        verify(mockAggregateRoot, times(1)).deleteUnsavedEvents();
        verify(mockAggregateRoot, atLeastOnce()).unsavedEvents();
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(mockAggregateRoot, atLeastOnce()).aggregateRootId();
        verify(mockAggregateRoot, atLeastOnce()).version();
        verify(secretStore, times(1)).store(any(), any(), any());
        verify(encryption, times(1)).generateNewSecret();
        verify(defaultAggregateRootRepository, times(1)).load(any(), any());
        verifyNoMoreInteractions(eventRepository, secretStore, encryption, aggregateRootMaterializedStateSerializer);
    }

    @Test
    public void should_purge_events_after_save() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        doReturn("newSecret").when(encryption).generateNewSecret();
        doReturn(0l).when(mockAggregateRoot).version();
        doReturn("{}").when(aggregateRootMaterializedStateSerializer).serialize(new UnsupportedSecret(), mockAggregateRoot);
        doReturn(mock(Secret.class)).when(secretStore).store("aggregateRootType", "aggregateRootId", "newSecret");
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).load(any(), any());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(aggregateRootMaterializedStateRepository, times(1))
                .persist(new DefaultAggregateRootMaterializedState(mockAggregateRoot.aggregateRootId(), 0l, "{}"));
        verify(mockAggregateRoot, times(1)).deleteUnsavedEvents();
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootType();
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(mockAggregateRoot, atLeastOnce()).version();
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(new UnsupportedSecret(), mockAggregateRoot);
        verify(secretStore, times(1)).store(any(), any(), any());
        verify(encryption, times(1)).generateNewSecret();
        verify(defaultAggregateRootRepository, times(1)).load(any(), any());
        verifyNoMoreInteractions(secretStore, encryption, aggregateRootMaterializedStateSerializer, aggregateRootMaterializedStateRepository);
    }

    @Test
    public void should_save_aggregate() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        doReturn("newSecret").when(encryption).generateNewSecret();
        doReturn(0l).when(mockAggregateRoot).version();
        doReturn("{}").when(aggregateRootMaterializedStateSerializer).serialize(new UnsupportedSecret(), mockAggregateRoot);
        doReturn(mock(Secret.class)).when(secretStore).store("aggregateRootType", "aggregateRootId", "newSecret");
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).load(any(), any());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(new UnsupportedSecret(), mockAggregateRoot);
        verify(aggregateRootMaterializedStateRepository, times(1))
                .persist(new DefaultAggregateRootMaterializedState(mockAggregateRoot.aggregateRootId(), 0l, "{}"));
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootType();
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(mockAggregateRoot, atLeastOnce()).version();
        verify(secretStore, times(1)).store(any(), any(), any());
        verify(encryption, times(1)).generateNewSecret();
        verify(defaultAggregateRootRepository, times(1)).load(any(), any());
        verifyNoMoreInteractions(secretStore, encryption, aggregateRootMaterializedStateSerializer, aggregateRootMaterializedStateRepository);
    }

    @Test
    public void should_load_aggregateRoot() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mock(AggregateRootEvent.class));
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).read(aggregateRoot.getClass().getSimpleName(), "aggregateRootId");
        doReturn(aggregateRootEvents).when(eventRepository).loadOrderByVersionASC("aggregateRootId",aggregateRoot.getClass().getSimpleName(), mockSecret);
        doReturn(aggregateRoot).when(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);

        // When
        final AggregateRoot aggregateRootLoaded = defaultAggregateRootRepository.load("aggregateRootId", AggregateRoot.class);

        // Then
        assertEquals(aggregateRoot, aggregateRootLoaded);
        verify(secretStore, times(1)).read(any(), any());
        verify(aggregateRoot, times(1)).loadFromHistory(aggregateRootEvents);
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), anyString(), any());
        verify(defaultAggregateRootRepository, times(1)).createNewInstance(any());
    }

    @Test
    public void should_throw_exception_when_no_events_are_presents() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);
        doReturn(Collections.emptyList()).when(eventRepository).loadOrderByVersionASC("aggregateRootId", aggregateRoot.getClass().getName(), mock(Secret.class));
        doReturn(aggregateRoot).when(defaultAggregateRootRepository).createNewInstance(AggregateRoot.class);

        // When && Then
        Assertions.assertThrows(UnknownAggregateRootException.class, () -> {
            defaultAggregateRootRepository.load("aggregateRootId", AggregateRoot.class);
        });

        verify(aggregateRoot, never()).loadFromHistory(anyList());
        verify(defaultAggregateRootRepository, times(1)).createNewInstance(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), anyString(), any());
    }

    @Test
    public void should_create_secret_from_secret_store_when_event_is_the_first_one() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        doReturn(0l).when(mockAggregateRoot).version();
        doReturn("{}").when(aggregateRootMaterializedStateSerializer).serialize(new UnsupportedSecret(), mockAggregateRoot);
        final Secret mockSecret = mock(Secret.class);
        doReturn("newSecret").when(encryption).generateNewSecret();
        doReturn(mockSecret).when(secretStore).store("aggregateRootType", "aggregateRootId", "newSecret");
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).load(any(), any());
        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(secretStore, times(1)).store("aggregateRootType", "aggregateRootId", "newSecret");
        verify(secretStore, times(0)).read(any(), any());
        verify(encryption, times(1)).generateNewSecret();
        verify(mockAggregateRoot, atLeastOnce()).version();
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(any(), any());
        verify(defaultAggregateRootRepository, times(1)).load(any(), any());
        verifyNoMoreInteractions(secretStore);
    }

    @Test
    public void should_reuse_secret_from_secret_store_when_event_is_not_the_first_one() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        final Secret mockSecret = mock(Secret.class);
        doReturn(1l).when(mockAggregateRoot).version();
        doReturn("{}").when(aggregateRootMaterializedStateSerializer).serialize(new UnsupportedSecret(), mockAggregateRoot);
        doReturn(mockSecret).when(secretStore).read("aggregateRootType", "aggregateRootId");
        doReturn(mockAggregateRoot).when(defaultAggregateRootRepository).load(any(), any());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(secretStore, times(1)).read("aggregateRootType", "aggregateRootId");
        verify(secretStore, times(0)).store(any(), any(), any());
        verify(mockAggregateRoot, atLeastOnce()).version();
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(any(), any());
        verify(defaultAggregateRootRepository, times(1)).load(any(), any());
        verifyNoMoreInteractions(secretStore);
    }
}
