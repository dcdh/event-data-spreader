package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SecretStore;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.runtime.Startup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

@Startup
@ApplicationScoped
public class AgroalDataSourceSecretStore implements SecretStore {

    private static final String POSTGRESQL_DDL_FILE = "/sql/secret-store-postgresql.ddl";

    final AgroalDataSource secretStoreDataSource;

    public AgroalDataSourceSecretStore(@DataSource("secret-store") final AgroalDataSource secretStoreDataSource) {
        this.secretStoreDataSource = Objects.requireNonNull(secretStoreDataSource);
    }

    @PostConstruct
    public void initSecretStoreTable() {
        final InputStream ddlResource = this.getClass().getResourceAsStream(POSTGRESQL_DDL_FILE);
        try (final Scanner scanner = new Scanner(ddlResource).useDelimiter("!!");
             final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            while (scanner.hasNext()) {
                final String ddlEntry = scanner.next().trim();
                if (!ddlEntry.isEmpty()) {
                    stmt.executeUpdate(ddlEntry);
                }
            }
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
