package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public final class TestAggregateRoot extends AggregateRoot {

        public TestAggregateRoot() {}

    }

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
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(aggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        doReturn("newSecret").when(encryption).generateNewSecret();
        doReturn(0l).when(aggregateRoot).version();
        doReturn("{}").when(aggregateRootMaterializedStateSerializer).serialize(aggregateRoot);
        final AggregateRootEvent aggregateRootEvent = mock(AggregateRootEvent.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEvent.version()).thenReturn(0l);
        final Optional<AggregateRootSecret> aggregateRootSecret = mock(Optional.class);
        doReturn(aggregateRootSecret).when(secretStore).read(any(), eq("aggregateRootId"));
        doReturn(singletonList(aggregateRootEvent)).when(eventRepository)
                .loadOrderByVersionASC(eq("aggregateRootId"), any(), eq(aggregateRootSecret), eq(0l));
        final List<AggregateRootEvent> unsavedAggregateRootEvents = singletonList(aggregateRootEvent);

        doReturn(unsavedAggregateRootEvents).when(aggregateRoot).unsavedEvents();
        final AggregateRootSecret storedAggregateRootSecret = mock(AggregateRootSecret.class);
        doReturn(storedAggregateRootSecret).when(secretStore).store("aggregateRootType", "aggregateRootId", "newSecret");

        final AggregateRoot createdAggregateRoot = mock(AggregateRoot.class);
        doReturn(createdAggregateRoot).when(defaultAggregateRootRepository).createNewInstance(any());

        // When
        final AggregateRoot aggregateRootSaved = defaultAggregateRootRepository.save(aggregateRoot);

        // Then
        assertEquals(aggregateRoot, aggregateRootSaved);
        verify(eventRepository, times(1)).save(eq(singletonList(aggregateRootEvent)), eq(Optional.of(storedAggregateRootSecret)));
        verify(eventRepository, times(1)).saveMaterializedState(eq(createdAggregateRoot), eq(Optional.of(storedAggregateRootSecret)));

        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any(), any(), any());
        verify(secretStore, times(1)).read(any(), any());
        verify(defaultAggregateRootRepository, times(1)).createNewInstance(any());
        verify(aggregateRoot, times(1)).deleteUnsavedEvents();
        verify(aggregateRoot, atLeastOnce()).unsavedEvents();
        verify(aggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(aggregateRoot, atLeastOnce()).aggregateRootId();
        verify(aggregateRoot, atLeastOnce()).version();
        verify(aggregateRootEvent, atLeastOnce()).version();
        verify(secretStore, times(1)).store(any(), any(), any());
        verify(encryption, times(1)).generateNewSecret();
        verifyNoMoreInteractions(eventRepository);
    }

    @Test
    public void should_purge_events_after_save() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(aggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        doReturn("newSecret").when(encryption).generateNewSecret();
        doReturn(0l).when(aggregateRoot).version();
        doReturn("{}").when(aggregateRootMaterializedStateSerializer).serialize(aggregateRoot);
        doReturn(mock(AggregateRootSecret.class)).when(secretStore).store("aggregateRootType", "aggregateRootId", "newSecret");

        // When
        defaultAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(aggregateRoot);
        verify(aggregateRootMaterializedStateRepository, times(1))
                .persist(new DefaultAggregateRootMaterializedState(aggregateRoot.aggregateRootId(), 0l, "{}"));
        verify(aggregateRoot, times(1)).deleteUnsavedEvents();
        verify(aggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootType();
        verify(aggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(aggregateRoot, atLeastOnce()).version();
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(aggregateRoot);
        verify(secretStore, times(1)).store(any(), any(), any());
        verify(encryption, times(1)).generateNewSecret();
        verifyNoMoreInteractions(secretStore, encryption, aggregateRootMaterializedStateSerializer, aggregateRootMaterializedStateRepository);
    }

    @Test
    public void should_save_aggregate() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(aggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        doReturn("newSecret").when(encryption).generateNewSecret();
        doReturn(0l).when(aggregateRoot).version();
        doReturn("{}").when(aggregateRootMaterializedStateSerializer).serialize(aggregateRoot);
        doReturn(mock(AggregateRootSecret.class)).when(secretStore).store("aggregateRootType", "aggregateRootId", "newSecret");

        // When
        defaultAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(aggregateRoot);
        verify(aggregateRootMaterializedStateRepository, times(1))
                .persist(new DefaultAggregateRootMaterializedState(aggregateRoot.aggregateRootId(), 0l, "{}"));
        verify(aggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootType();
        verify(aggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(aggregateRoot, atLeastOnce()).version();
        verify(secretStore, times(1)).store(any(), any(), any());
        verify(encryption, times(1)).generateNewSecret();
        verifyNoMoreInteractions(secretStore, encryption, aggregateRootMaterializedStateSerializer, aggregateRootMaterializedStateRepository);
    }

    @Test
    public void should_load_aggregateRoot() {
        // Given
        final TestAggregateRoot aggregateRoot = mock(TestAggregateRoot.class);
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mock(AggregateRootEvent.class));
        doReturn(aggregateRootEvents).when(eventRepository).loadOrderByVersionASC("aggregateRootId","TestAggregateRoot", Optional.empty());
        doReturn(aggregateRoot).when(defaultAggregateRootRepository).createNewInstance(TestAggregateRoot.class);

        // When
        final TestAggregateRoot aggregateRootLoaded = defaultAggregateRootRepository.load("aggregateRootId", TestAggregateRoot.class);

        // Then
        assertEquals(aggregateRoot, aggregateRootLoaded);
        verify(aggregateRoot, times(1)).loadFromHistory(aggregateRootEvents);
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), anyString(), any());
        verify(defaultAggregateRootRepository, times(1)).createNewInstance(any());
    }

    @Test
    public void should_throw_exception_when_no_events_are_presents() {
        // Given
        final TestAggregateRoot aggregateRoot = mock(TestAggregateRoot.class);
        doReturn(Collections.emptyList()).when(eventRepository).loadOrderByVersionASC("aggregateRootId", aggregateRoot.getClass().getName(), Optional.empty());
        doReturn(aggregateRoot).when(defaultAggregateRootRepository).createNewInstance(TestAggregateRoot.class);

        // When && Then
        Assertions.assertThrows(UnknownAggregateRootException.class, () -> {
            defaultAggregateRootRepository.load("aggregateRootId", TestAggregateRoot.class);
        });

        verify(aggregateRoot, never()).loadFromHistory(anyList());
        verify(defaultAggregateRootRepository, times(1)).createNewInstance(any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), anyString(), any());
    }

    @Test
    public void should_create_secret_from_secret_store_when_event_is_the_first_one() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(aggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        doReturn(0l).when(aggregateRoot).version();
        doReturn("{}").when(aggregateRootMaterializedStateSerializer).serialize(aggregateRoot);
        final AggregateRootSecret storedAggregateRootSecret = mock(AggregateRootSecret.class);
        doReturn("newSecret").when(encryption).generateNewSecret();
        doReturn(storedAggregateRootSecret).when(secretStore).store("aggregateRootType", "aggregateRootId", "newSecret");

        // When
        defaultAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(secretStore, times(1)).store("aggregateRootType", "aggregateRootId", "newSecret");
        verify(secretStore, times(0)).read(any(), any());
        verify(encryption, times(1)).generateNewSecret();
        verify(aggregateRoot, atLeastOnce()).version();
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(any());
        verifyNoMoreInteractions(secretStore);
    }

    @Test
    public void should_reuse_secret_from_secret_store_when_event_is_not_the_first_one() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(aggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        doReturn(1l).when(aggregateRoot).version();
        doReturn("{}").when(aggregateRootMaterializedStateSerializer).serialize(aggregateRoot);

        // When
        defaultAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(secretStore, times(1)).read("aggregateRootType", "aggregateRootId");
        verify(secretStore, times(0)).store(any(), any(), any());
        verify(aggregateRoot, atLeastOnce()).version();
        verify(aggregateRootMaterializedStateSerializer, times(1)).serialize(any());
        verifyNoMoreInteractions(secretStore);
    }
}
