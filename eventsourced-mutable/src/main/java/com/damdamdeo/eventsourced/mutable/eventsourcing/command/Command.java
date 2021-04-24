package com.damdamdeo.eventsourced.mutable.eventsourcing.command;

public interface Command {

    CommandLockingType commandLockingType();

    String aggregateRootId();

}
