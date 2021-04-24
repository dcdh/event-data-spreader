package com.damdamdeo.eventsourced.mutable.eventsourcing.lock;

import java.util.List;

public interface LockingMechanism {

    void lockUntilReleased(List<String> locksNames);

    void unlock(List<String> locksNames);

}
