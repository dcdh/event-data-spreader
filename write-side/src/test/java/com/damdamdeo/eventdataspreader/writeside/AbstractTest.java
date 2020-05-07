package com.damdamdeo.eventdataspreader.writeside;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractTest {

    @Inject
    EntityManager entityManager;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @Inject
    @DataSource("aggregate-root-projection-event-store")
    AgroalDataSource aggregateRootProjectionEventStoreDataSource;

    @BeforeEach
    @Transactional
    public void setup() {
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE SECRET_STORE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (final Connection con = aggregateRootProjectionEventStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE AGGREGATE_ROOT_PROJECTION");
            stmt.executeUpdate("TRUNCATE TABLE EVENT");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        entityManager.createQuery("DELETE FROM EventConsumerConsumedEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM EventConsumedEntity").executeUpdate();
    }

}
