package com.damdamdeo.eventsourced.mutable.api.eventsourcing.command;

public interface Command {

    CommandLockingType commandLockingType();

    String aggregateRootId();

}
