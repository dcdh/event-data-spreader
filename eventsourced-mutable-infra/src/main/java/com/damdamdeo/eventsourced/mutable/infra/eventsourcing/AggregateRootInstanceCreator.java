package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AggregateRootInstanceCreator {

    public <T extends AggregateRoot> T createNewInstance(final Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
