package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SecretStore;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class AgroalDataSourceSecretStore implements SecretStore {

    final AgroalDataSource secretStoreDataSource;

    public AgroalDataSourceSecretStore(@DataSource("secret-store") final AgroalDataSource secretStoreDataSource) {
        this.secretStoreDataSource = Objects.requireNonNull(secretStoreDataSource);
    }

    @PostConstruct
    public void initSecretStoreTable() {
        final String createTable = "CREATE TABLE IF NOT EXISTS SecretStore (\n" +
                "    aggregateRootType varchar(255),\n" +
                "    aggregateRootId   varchar(255),\n" +
                "    secret            varchar(255),\n" +
                "    CONSTRAINT aggregateRootType_aggregateRootId PRIMARY KEY(aggregateRootType,aggregateRootId)\n" +
                ");";
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate(createTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @CacheInvalidate(cacheName = "secret-cache")
    public EncryptedEventSecret store(@CacheKey final String aggregateRootType,
                      @CacheKey final String aggregateRootId,
                      final String secret) {
        final String storeSecretStatement = String.format("INSERT INTO SecretStore (aggregateRootType, aggregateRootId, secret) VALUES ('%s', '%s', '%s')",
                aggregateRootType, aggregateRootId, secret);
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate(storeSecretStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new JdbcEncryptedEventSecret(aggregateRootType, aggregateRootId, secret);
    }

    @Override
    @CacheResult(cacheName = "secret-cache")
    public Optional<EncryptedEventSecret> read(final String aggregateRootType, final String aggregateRootId) {
        final String getSecretStatement = String.format("SELECT aggregateRootType, aggregateRootId, secret FROM SecretStore WHERE aggregateRootType = '%s' AND aggregateRootId = '%s'",
                aggregateRootType, aggregateRootId);
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery(getSecretStatement)) {
            if (resultSet.next()) {
                return Optional.of(new JdbcEncryptedEventSecret(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
