package com.damdamdeo.eventdataspreader.writeside.command.api;

public interface Command {

    String aggregateId();

    boolean exactlyOnceCommandExecution();

}
