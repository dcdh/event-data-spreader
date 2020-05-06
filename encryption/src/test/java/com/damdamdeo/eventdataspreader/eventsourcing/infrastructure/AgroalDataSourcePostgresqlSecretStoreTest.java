package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SecretStore;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class AgroalDataSourcePostgresqlSecretStoreTest {

    @Inject
    SecretStore secretStore;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @BeforeEach
    public void setup() {
        final String truncate = "TRUNCATE TABLE SECRET_STORE";
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate(truncate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void should_store_and_retrieve() {
        // Given
        final String secret = "Hello World";

        // When
        final EncryptedEventSecret storedSecret = secretStore.store("aggregateRootType", "aggregateRootId", secret);
        final EncryptedEventSecret readSecret = secretStore.read("aggregateRootType", "aggregateRootId").get();

        // Then
        assertEquals(new JdbcEncryptedEventSecret("aggregateRootType", "aggregateRootId", "Hello World"),
                readSecret);
        assertEquals(storedSecret, readSecret);
    }

    @Test
    public void should_return_optional_empty_if_secret_does_not_exists() {
        assertFalse(secretStore.read("aggregateRootType", "aggregateRootId").isPresent());
    }

}
