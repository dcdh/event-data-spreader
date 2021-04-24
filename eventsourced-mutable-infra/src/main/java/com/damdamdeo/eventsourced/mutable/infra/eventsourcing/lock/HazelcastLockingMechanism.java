package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.lock;

import com.damdamdeo.eventsourced.mutable.eventsourcing.lock.LockingMechanism;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@ApplicationScoped
public final class HazelcastLockingMechanism implements LockingMechanism {

    private final HazelcastInstance hazelcastClient;

    public HazelcastLockingMechanism(final HazelcastInstance hazelcastClient) {
        this.hazelcastClient = Objects.requireNonNull(hazelcastClient);
    }

    @Override
    public void lockUntilReleased(final List<String> locksNames) {
        executeOnEachLock(locksNames, FencedLock::lock);
    }

    @Override
    public void unlock(final List<String> locksNames) {
        executeOnEachLock(locksNames, FencedLock::unlock);
    }

    private void executeOnEachLock(final List<String> locksNames, final Consumer<FencedLock> fencedLockConsumer) {
        Objects.requireNonNull(locksNames);
        Objects.requireNonNull(fencedLockConsumer);
        locksNames.stream()
                .filter(lockName -> lockName != null)
                .map(lockName -> hazelcastClient.getCPSubsystem().getLock(lockName))
                .forEach(fencedLockConsumer);
    }

}
