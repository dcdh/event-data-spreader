package com.damdamdeo.eventsourced.encryption.infra;

import com.damdamdeo.eventsourced.encryption.api.MissingSecret;
import com.damdamdeo.eventsourced.encryption.api.PresentSecret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.cache.runtime.CacheRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class PostgreSQLSecretStoreTest {

    @Inject
    SecretStore secretStore;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @Inject
    CacheRepository cacheRepository;

    @BeforeEach
    @AfterEach
    public void setup() {
        final String truncate = "TRUNCATE TABLE SECRET_STORE";
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate(truncate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // flush cache
        cacheRepository.getCache("secret-cache").invalidateAll();
    }

    @Test
    public void should_tables_be_initialised_at_application_startup() {
        Assertions.assertDoesNotThrow(() -> {
            try (final Connection con = secretStoreDataSource.getConnection();
                 final Statement stmt = con.createStatement();
                 final ResultSet rsSecretStore = stmt.executeQuery("SELECT * FROM SECRET_STORE")) {
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void should_store_and_retrieve_secret() {
        // Given
        final String secret = "Hello World";
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();

        // When
        final Secret storedSecret = secretStore.store(aggregateRootId, secret);
        final Secret readSecret = secretStore.read(aggregateRootId);

        // Then
        assertEquals(new PresentSecret("Hello World"), readSecret);
        assertEquals(storedSecret, readSecret);
        verify(aggregateRootId, atLeastOnce()).aggregateRootType();
        verify(aggregateRootId, atLeastOnce()).aggregateRootId();
    }

    @Test
    public void should_return_missing_secret_if_secret_does_not_exists() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();

        // When
        final Secret secret = secretStore.read(aggregateRootId);

        // Then
        assertEquals(new MissingSecret(), secret);
        verify(aggregateRootId, atLeastOnce()).aggregateRootType();
        verify(aggregateRootId, atLeastOnce()).aggregateRootId();
    }

}
