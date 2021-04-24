package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.*;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.eventsourcing.*;
import com.damdamdeo.eventsourced.mutable.eventsourcing.serialization.AggregateRootMaterializedStatesDeSerializer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

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
    AggregateRootMaterializedStatesDeSerializer aggregateRootMaterializedStatesDeSerializer;

    @InjectMock
    SecretStore secretStore;

    @InjectMock
    @AESEncryptionQualifier
    Encryption aesEncryption;

    @Test
    public void should_fail_fast_when_save_null_aggregate_root() {
        // Given
        // When && Then
        assertThrows(NullPointerException.class, () -> defaultAggregateRootRepository.save(null));
    }

    @Test
    public void should_create_new_instance_when_saving_new_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        final AggregateRoot mockLastSavedAggregateRootState = mock(AggregateRoot.class);
        doReturn(mockLastSavedAggregateRootState).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStatesDeSerializer).serialize(any(), anyBoolean());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(aggregateRootInstanceCreator, atLeastOnce()).createNewInstance(mockAggregateRoot.getClass(), "aggregateRootId");
        verify(mockAggregateRoot.aggregateRootId(), times(1)).aggregateRootId();
        verify(aggregateRootMaterializedStatesDeSerializer, times(1)).serialize(any(), anyBoolean());
    }

    @Test
    public void should_load_same_aggregate_root_when_saving_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        final AggregateRootId mockAggregateRootId = mock(AggregateRootId.class);
        doReturn(mockAggregateRootId).when(mockAggregateRoot).aggregateRootId();
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).store(any(), any());
        final AggregateRoot mockLastSavedAggregateRootState = mock(AggregateRoot.class);
        doReturn(mockLastSavedAggregateRootState).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStatesDeSerializer).serialize(any(), anyBoolean());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(eventRepository, times(1)).loadOrderByVersionASC(mockAggregateRootId);
        verify(mockAggregateRoot, atLeast(1)).aggregateRootId();
        verify(aggregateRootMaterializedStatesDeSerializer, times(1)).serialize(any(), anyBoolean());
        verify(secretStore).store(any(), any());
        verify(aggregateRootInstanceCreator, atLeastOnce()).createNewInstance(any(), any());
    }

    @Test
    public void should_serialize_unencrypted_aggregate_root_materialize_state() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).store(any(), any());
        final AggregateRoot mockLastSavedAggregateRootState = mock(AggregateRoot.class);
        doReturn(mockLastSavedAggregateRootState).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStatesDeSerializer).serialize(any(), anyBoolean());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(aggregateRootMaterializedStatesDeSerializer, times(1)).serialize(mockAggregateRoot, false);
        verify(aggregateRootMaterializedStatesDeSerializer, times(1)).serialize(any(), anyBoolean());
        verify(secretStore).store(any(), any());
        verify(aggregateRootInstanceCreator, atLeastOnce()).createNewInstance(any(), any());
    }

    @Test
    public void should_save_aggregate_root_materialize_state() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).store(any(), any());
        final AggregateRoot mockLastSavedAggregateRootState = mock(AggregateRoot.class);
        doReturn(mockLastSavedAggregateRootState).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStatesDeSerializer).serialize(any(), anyBoolean());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(aggregateRootMaterializedStateRepository, times(1)).persist(new DefaultAggregateRootMaterializedState(mockAggregateRoot, "serializedAggregateRoot"));
        verify(aggregateRootMaterializedStatesDeSerializer, times(1)).serialize(any(), anyBoolean());
        verify(secretStore).store(any(), any());
        verify(aggregateRootInstanceCreator, atLeastOnce()).createNewInstance(any(), any());
    }

    @Test
    public void should_generate_new_secret_when_saving_new_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.version()).thenReturn(0l);
        doReturn("newSecret").when(aesEncryption).generateNewSecret();
        final AggregateRoot mockLastSavedAggregateRootState = mock(AggregateRoot.class);
        doReturn(mockLastSavedAggregateRootState).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStatesDeSerializer).serialize(any(), anyBoolean());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(secretStore, times(1)).store(mockAggregateRoot.aggregateRootId(), "newSecret");
        verify(aesEncryption, times(1)).generateNewSecret();
        verify(aggregateRootMaterializedStatesDeSerializer, times(1)).serialize(any(), anyBoolean());
        verify(mockAggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(mockAggregateRoot, atLeastOnce()).version();
        verify(aggregateRootInstanceCreator, atLeastOnce()).createNewInstance(any(), any());
        verifyNoMoreInteractions(secretStore);
    }

    @Test
    public void should_apply_events_on_loaded_aggregate_root_when_saving_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).store(any(), any());
        final AggregateRoot mockLastSavedAggregateRootState = mock(AggregateRoot.class);
        doReturn(mockLastSavedAggregateRootState).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStatesDeSerializer).serialize(any(), anyBoolean());

        final AggregateRootEvent mockAggregateRootEvent = mock(AggregateRootEvent.class);
        doReturn("eventType").when(mockAggregateRootEvent).eventType();
        final AggregateRootEventPayload mockAggregateRootEventPayload = mock(AggregateRootEventPayload.class);
        doReturn(mockAggregateRootEventPayload).when(mockAggregateRootEvent).eventPayload();
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mockAggregateRootEvent);
        doReturn(aggregateRootEvents).when(mockAggregateRoot).unsavedEvents();

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(mockLastSavedAggregateRootState, times(1)).apply("eventType", mockAggregateRootEventPayload);
        verify(mockAggregateRootEvent, times(1)).eventType();
        verify(mockAggregateRootEvent, times(1)).eventPayload();
        verify(aggregateRootMaterializedStatesDeSerializer, times(1)).serialize(any(), anyBoolean());
        verify(secretStore).store(any(), any());
        verify(aggregateRootInstanceCreator, atLeastOnce()).createNewInstance(any(), any());
    }

    @Test
    public void should_delete_event_applied_on_loaded_aggregate_root_when_saving_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).store(any(), any());
        final AggregateRoot mockLastSavedAggregateRootState = mock(AggregateRoot.class);
        doReturn(mockLastSavedAggregateRootState).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStatesDeSerializer).serialize(any(), anyBoolean());

        final AggregateRootEvent mockAggregateRootEvent = mock(AggregateRootEvent.class);
        doReturn("eventType").when(mockAggregateRootEvent).eventType();
        final AggregateRootEventPayload mockAggregateRootEventPayload = mock(AggregateRootEventPayload.class);
        doReturn(mockAggregateRootEventPayload).when(mockAggregateRootEvent).eventPayload();
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mockAggregateRootEvent);
        doReturn(aggregateRootEvents).when(mockAggregateRoot).unsavedEvents();

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(mockLastSavedAggregateRootState, times(1)).deleteUnsavedEvents();
        verify(mockAggregateRootEvent, times(1)).eventType();
        verify(mockAggregateRootEvent, times(1)).eventPayload();
        verify(aggregateRootMaterializedStatesDeSerializer, times(1)).serialize(any(), anyBoolean());
        verify(secretStore).store(any(), any());
        verify(aggregateRootInstanceCreator, atLeastOnce()).createNewInstance(any(), any());
    }

    @Test
    public void should_save_event_with_representation_materialized_state_when_saving_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).store(any(), any());
        final AggregateRoot mockLastSavedAggregateRootState = mock(AggregateRoot.class);
        doReturn(mockLastSavedAggregateRootState).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStatesDeSerializer).serialize(any(), anyBoolean());

        final AggregateRootEvent mockAggregateRootEvent = mock(AggregateRootEvent.class);
        doReturn("eventType").when(mockAggregateRootEvent).eventType();
        final AggregateRootEventPayload mockAggregateRootEventPayload = mock(AggregateRootEventPayload.class);
        doReturn(mockAggregateRootEventPayload).when(mockAggregateRootEvent).eventPayload();
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mockAggregateRootEvent);
        doReturn(aggregateRootEvents).when(mockAggregateRoot).unsavedEvents();

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(eventRepository, times(1)).save(mockAggregateRootEvent, mockLastSavedAggregateRootState);
        verify(mockAggregateRootEvent, times(1)).eventType();
        verify(mockAggregateRootEvent, times(1)).eventPayload();
        verify(aggregateRootMaterializedStatesDeSerializer, times(1)).serialize(any(), anyBoolean());
        verify(secretStore).store(any(), any());
        verify(aggregateRootInstanceCreator, atLeastOnce()).createNewInstance(any(), any());
    }

    @Test
    public void should_purge_events_after_saving_when_saving_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).store(any(), any());
        final AggregateRoot mockLastSavedAggregateRootState = mock(AggregateRoot.class);
        doReturn(mockLastSavedAggregateRootState).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn("serializedAggregateRoot").when(aggregateRootMaterializedStatesDeSerializer).serialize(any(), anyBoolean());

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        verify(mockAggregateRoot).deleteUnsavedEvents();
        verify(aggregateRootMaterializedStatesDeSerializer, times(1)).serialize(any(), anyBoolean());
        verify(secretStore).store(any(), any());
        verify(aggregateRootInstanceCreator, atLeastOnce()).createNewInstance(any(), any());
    }

    // Loading aggregate root

    @Test
    public void should_create_new_instance_when_loading_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        final AggregateRootId mockAggregateRootId = mock(AggregateRootId.class);
        doReturn(mockAggregateRootId).when(mockAggregateRoot).aggregateRootId();
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass());

        // Then
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(mockAggregateRoot.getClass(), "aggregateRootId");
        verify(eventRepository, times(1)).loadOrderByVersionASC(any());
        verify(mockAggregateRoot, times(1)).aggregateRootId();
    }

    @Test
    public void should_load_aggregate_root_events_order_by_version_asc_when_loading_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        final AggregateRootId mockAggregateRootId = mock(AggregateRootId.class);
        doReturn(mockAggregateRootId).when(mockAggregateRoot).aggregateRootId();
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass());

        // Then
        verify(eventRepository, times(1)).loadOrderByVersionASC(mockAggregateRootId);
        verify(mockAggregateRoot, times(1)).aggregateRootId();
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any(), any());
    }

    @Test
    public void should_apply_loaded_events_on_aggregate_root() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mock(AggregateRootEvent.class));
        doReturn(aggregateRootEvents).when(eventRepository).loadOrderByVersionASC(any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass());

        // Then
        verify(mockAggregateRoot, times(1)).loadFromHistory(aggregateRootEvents);
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any(), any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any());
    }

    @Test
    public void should_load_aggregate_root_from_events() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mock(AggregateRootEvent.class));
        doReturn(aggregateRootEvents).when(eventRepository).loadOrderByVersionASC(any());

        // When
        final AggregateRoot mockAggregateRootLoaded = defaultAggregateRootRepository.load("aggregateRootId",
                mockAggregateRoot.getClass());

        // Then
        assertEquals(mockAggregateRoot, mockAggregateRootLoaded);
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any(), any());
        verify(mockAggregateRoot, times(1)).loadFromHistory(aggregateRootEvents);
        verify(eventRepository, times(1)).loadOrderByVersionASC(any());
    }

    @Test
    public void should_throw_exception_when_loading_an_aggregate_root_without_events() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mock(AggregateRootId.class)).when(mockAggregateRoot).aggregateRootId();
        doReturn(Collections.emptyList()).when(eventRepository).loadOrderByVersionASC(any());
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(any(), any());

        // When && Then
        assertThrows(UnknownAggregateRootException.class, () -> defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass()));

        verify(mockAggregateRoot, never()).loadFromHistory(anyList());
        verify(mockAggregateRoot, atLeastOnce()).aggregateRootId();
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any(), any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any());
    }

    // Loading aggregate root by version

    @Test
    public void should_create_new_instance_when_loading_aggregate_root_by_version() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        final AggregateRootId mockAggregateRootId = mock(AggregateRootId.class);
        doReturn(mockAggregateRootId).when(mockAggregateRoot).aggregateRootId();
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass(), 1l);

        // Then
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(mockAggregateRoot.getClass(), "aggregateRootId");
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any());
        verify(mockAggregateRoot, times(1)).aggregateRootId();
    }

    @Test
    public void should_load_aggregate_root_events_order_by_version_asc_when_loading_aggregate_root_by_version() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        final AggregateRootId mockAggregateRootId = mock(AggregateRootId.class);
        doReturn(mockAggregateRootId).when(mockAggregateRoot).aggregateRootId();
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass(), 1l);

        // Then
        verify(eventRepository, times(1)).loadOrderByVersionASC(mockAggregateRootId, 1l);
        verify(mockAggregateRoot, times(1)).aggregateRootId();
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any(), any());
    }

    @Test
    public void should_apply_loaded_events_on_aggregate_root_by_version() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mock(AggregateRootEvent.class));
        doReturn(aggregateRootEvents).when(eventRepository).loadOrderByVersionASC(any(), any());

        // When
        defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass(), 1l);

        // Then
        verify(mockAggregateRoot, times(1)).loadFromHistory(aggregateRootEvents);
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any(), any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any());
    }

    @Test
    public void should_load_aggregate_root_from_events_by_version() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(any(), any());
        doReturn(singletonList(mock(AggregateRootEvent.class))).when(eventRepository).loadOrderByVersionASC(any(), any());

        // When
        final AggregateRoot mockAggregateRootLoaded = defaultAggregateRootRepository.load("aggregateRootId",
                mockAggregateRoot.getClass(), 1l);

        // Then
        assertEquals(mockAggregateRoot, mockAggregateRootLoaded);
        verify(mockAggregateRoot, times(1)).loadFromHistory(any());
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any(), any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any());
    }

    @Test
    public void should_throw_exception_when_loading_an_aggregate_root_without_events_by_version() {
        // Given
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mock(AggregateRootId.class)).when(mockAggregateRoot).aggregateRootId();
        doReturn(Collections.emptyList()).when(eventRepository).loadOrderByVersionASC(any(), any());
        doReturn(mockAggregateRoot).when(aggregateRootInstanceCreator).createNewInstance(any(), any());

        // When && Then
        assertThrows(UnknownAggregateRootException.class, () -> defaultAggregateRootRepository.load("aggregateRootId", mockAggregateRoot.getClass(), 1l));

        verify(mockAggregateRoot, never()).loadFromHistory(anyList());
        verify(mockAggregateRoot, atLeastOnce()).aggregateRootId();
        verify(aggregateRootInstanceCreator, times(1)).createNewInstance(any(), any());
        verify(eventRepository, times(1)).loadOrderByVersionASC(any(), any());
    }

    // findMaterializedState

    @Test
    public void should_find_materialized_state_repository_when_finding_materialized_state() {
        // Given

        // When
        defaultAggregateRootRepository.findMaterializedState("aggregateRootId", AggregateRoot.class);

        // Then
        verify(aggregateRootMaterializedStateRepository, times(1)).find(new ApiAggregateRootId("aggregateRootId", AggregateRoot.class));
    }

    @Test
    public void should_return_deserialized_aggregate_root_when_finding_materialized_state() {
        // Given
        final AggregateRootMaterializedState mockAggregateRootMaterializedState = mock(AggregateRootMaterializedState.class);
        doReturn(mockAggregateRootMaterializedState).when(aggregateRootMaterializedStateRepository).find(any());
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class);
        doReturn(mockAggregateRoot).when(aggregateRootMaterializedStatesDeSerializer).deserialize(mockAggregateRootMaterializedState);

        // When
        final AggregateRoot aggregateRoot = defaultAggregateRootRepository.findMaterializedState("aggregateRootId", AggregateRoot.class);

        // Then
        assertEquals(mockAggregateRoot, aggregateRoot);
        verify(aggregateRootMaterializedStateRepository, times(1)).find(any());
        verify(aggregateRootMaterializedStatesDeSerializer, times(1)).deserialize(any());
    }

}
