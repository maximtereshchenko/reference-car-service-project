package com.andersenlab.application.storage;

import com.andersenlab.carservice.port.external.RepairerStore;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public final class ThreadSafeRepairerStore implements RepairerStore {

    private final RepairerStore original;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public ThreadSafeRepairerStore(RepairerStore original) {
        this.original = original;
    }

    @Override
    public void save(RepairerEntity repairerEntity) {
        withWriteLock(() -> original.save(repairerEntity));
    }

    @Override
    public Collection<RepairerEntity> findAllSorted(Sort sort) {
        return withReadLock(() -> original.findAllSorted(sort));
    }

    @Override
    public void delete(UUID id) {
        withWriteLock(() -> original.delete(id));
    }

    @Override
    public boolean has(UUID id) {
        return withReadLock(() -> original.has(id));
    }

    @Override
    public boolean hasRepairerWithStatusAssigned(UUID id) {
        return withReadLock(() -> original.hasRepairerWithStatusAssigned(id));
    }

    @Override
    public RepairerEntity getById(UUID id) {
        return withReadLock(() -> original.getById(id));
    }

    private void withWriteLock(Runnable action) {
        readWriteLock.writeLock().lock();
        try {
            action.run();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private <T> T withReadLock(Supplier<T> supplier) {
        readWriteLock.readLock().lock();
        try {
            return supplier.get();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
