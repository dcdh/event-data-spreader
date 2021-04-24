package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.mutable.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.eventsourcing.AggregateRootInstanceCreator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReflectionAggregateRootInstanceCreator implements AggregateRootInstanceCreator {

    @Override
    public <T extends AggregateRoot> T createNewInstance(final Class<T> clazz,
                                                         final String aggregateRootId) {
        try {
            return clazz.getDeclaredConstructor(String.class)
                    .newInstance(aggregateRootId);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
