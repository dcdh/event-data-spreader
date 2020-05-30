package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class CreatedAtProvider {

    public LocalDateTime createdAt() {
        return LocalDateTime.now();
    }

}
