package com.damdamdeo.eventdataspreader.writeside.command.api;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

public interface CommandHandler {

    AggregateRoot handle(Command command);

}
