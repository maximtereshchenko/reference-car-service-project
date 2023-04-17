package com.andersenlab.application.storage;

import com.andersenlab.carservice.port.external.RepairerStore;

import java.util.Collection;
import java.util.UUID;

public final class ThreadSafeRepairerStore implements RepairerStore {

    private final RepairerStore original;
    private final ReadWriteLockWrapper lock = new ReadWriteLockWrapper();

    public ThreadSafeRepairerStore(RepairerStore original) {
        this.original = original;
    }

    @Override
    public void save(RepairerEntity repairerEntity) {
        lock.withWriteLock(() -> original.save(repairerEntity));
    }

    @Override
    public Collection<RepairerEntity> findAllSorted(Sort sort) {
        return lock.withReadLock(() -> original.findAllSorted(sort));
    }

    @Override
    public void delete(UUID id) {
        lock.withWriteLock(() -> original.delete(id));
    }

    @Override
    public boolean has(UUID id) {
        return lock.withReadLock(() -> original.has(id));
    }

    @Override
    public boolean hasRepairerWithStatusAssigned(UUID id) {
        return lock.withReadLock(() -> original.hasRepairerWithStatusAssigned(id));
    }

    @Override
    public RepairerEntity getById(UUID id) {
        return lock.withReadLock(() -> original.getById(id));
    }


}
