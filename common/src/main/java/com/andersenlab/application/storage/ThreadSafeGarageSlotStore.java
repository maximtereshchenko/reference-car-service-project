package com.andersenlab.application.storage;

import com.andersenlab.carservice.port.external.GarageSlotStore;

import java.util.Collection;
import java.util.UUID;

public final class ThreadSafeGarageSlotStore implements GarageSlotStore {

    private final GarageSlotStore original;
    private final ReadWriteLockWrapper lock = new ReadWriteLockWrapper();

    public ThreadSafeGarageSlotStore(GarageSlotStore original) {
        this.original = original;
    }

    @Override
    public void save(GarageSlotEntity repairerEntity) {
        lock.withWriteLock(() -> original.save(repairerEntity));
    }

    @Override
    public Collection<GarageSlotEntity> findAllSorted(Sort sort) {
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
    public GarageSlotEntity getById(UUID id) {
        return lock.withReadLock(() -> original.getById(id));
    }

    @Override
    public boolean hasGarageSlotWithStatusAssigned(UUID id) {
        return lock.withReadLock(() -> original.hasGarageSlotWithStatusAssigned(id));
    }
}
