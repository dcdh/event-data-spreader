package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AggregateRootInstanceCreator {

    public <T extends AggregateRoot> T createNewInstance(final Class<T> clazz,
                                                         final String aggregateRootId) {
        try {
            return clazz.getDeclaredConstructor(AggregateRootId.class)
                    .newInstance(aggregateRootId);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
