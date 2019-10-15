package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import io.quarkus.arc.AlternativePriority;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EntityManagerProducers {

    @Inject
    EntityManagerFactory emf;

    @Produces
    @Dependent
    @AlternativePriority(1)
    public EntityManager produceEntityManager() {
        return emf.createEntityManager();
    }

}
