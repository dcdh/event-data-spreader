package com.damdamdeo.eventsourced.encryption.infra;

import com.damdamdeo.eventsourced.encryption.api.MissingSecret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.runtime.StartupEvent;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Objects;
import java.util.Scanner;

@ApplicationScoped
public class PostgreSQLSecretStore implements SecretStore {

    private static final String POSTGRESQL_DDL_FILE = "/sql/secret-store-postgresql.ddl";

    final AgroalDataSource secretStoreDataSource;

    public PostgreSQLSecretStore(@DataSource("secret-store") final AgroalDataSource secretStoreDataSource) {
        this.secretStoreDataSource = Objects.requireNonNull(secretStoreDataSource);
    }

    public void onStart(@Observes final StartupEvent ev) {
        final RetryPolicy<Object> retryPolicy = new RetryPolicy<>().handle(Exception.class)
                .withDelay(Duration.ofMillis(100))
                .withMaxRetries(100);
        final InputStream ddlResource = this.getClass().getResourceAsStream(POSTGRESQL_DDL_FILE);
        try (final Scanner scanner = new Scanner(ddlResource).useDelimiter("!!");
             final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            while (scanner.hasNext()) {
                final String ddlEntry = scanner.next().trim();
                if (!ddlEntry.isEmpty()) {
                    Failsafe.with(retryPolicy).run(() -> stmt.executeUpdate(ddlEntry));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @CacheInvalidate(cacheName = "secret-cache")
    @CacheResult(cacheName = "secret-cache")
    public Secret store(@CacheKey final AggregateRootId aggregateRootId,
                        final String secret) {
        final String storeSecretStatement = String.format("INSERT INTO SECRET_STORE (aggregateroottype, aggregaterootid, secret) VALUES ('%s', '%s', '%s')",
                aggregateRootId.aggregateRootType(),
                aggregateRootId.aggregateRootId(),
                secret);
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate(storeSecretStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new JdbcPresentAggregateRootSecret(aggregateRootId.aggregateRootType(),
                aggregateRootId.aggregateRootId(),
                secret)
                .aggregateRootSecret();
    }

    @Override
    @CacheResult(cacheName = "secret-cache")
    public Secret read(@CacheKey final AggregateRootId aggregateRootId) {
        final String getSecretStatement = String.format("SELECT aggregateroottype, aggregaterootid, secret FROM SECRET_STORE WHERE aggregateroottype = '%s' AND aggregaterootid = '%s'",
                aggregateRootId.aggregateRootType(),
                aggregateRootId.aggregateRootId());
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery(getSecretStatement)) {
            return resultSet.next() ? new JdbcPresentAggregateRootSecret(resultSet).aggregateRootSecret() : new MissingSecret();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
