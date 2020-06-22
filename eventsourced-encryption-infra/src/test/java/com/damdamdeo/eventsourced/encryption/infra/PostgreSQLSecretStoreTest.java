package com.damdamdeo.eventsourced.encryption.infra;

import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
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

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class PostgreSQLSecretStoreTest {

    @Inject
    SecretStore secretStore;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

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
    public void should_store_and_retrieve() {
        // Given
        final String secret = "Hello World";

        // When
        final AggregateRootSecret storedSecret = secretStore.store("aggregateRootType", "aggregateRootId", secret);
        final AggregateRootSecret readSecret = secretStore.read("aggregateRootType", "aggregateRootId");

        // Then
        assertEquals(new JdbcAggregateRootSecret("aggregateRootType", "aggregateRootId", "Hello World"),
                readSecret);
        assertEquals(storedSecret, readSecret);
    }

    @Test
    public void should_return_null_if_secret_does_not_exists() {
        assertNull(secretStore.read("aggregateRootType", "aggregateRootId"));
    }

}
