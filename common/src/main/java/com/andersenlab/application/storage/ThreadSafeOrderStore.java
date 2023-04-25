package com.andersenlab.application.storage;

import com.andersenlab.carservice.port.external.OrderStore;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public final class ThreadSafeOrderStore implements OrderStore {

    private final OrderStore original;
    private final ReadWriteLockWrapper lock;

    public ThreadSafeOrderStore(OrderStore original, ReadWriteLockWrapper lock) {
        this.original = original;
        this.lock = lock;
    }

    @Override
    public void save(OrderEntity orderEntity) {
        lock.withWriteLock(() -> original.save(orderEntity));
    }

    @Override
    public Optional<OrderEntity> findById(UUID id) {
        return lock.withReadLock(() -> original.findById(id));
    }

    @Override
    public Collection<OrderProjection> findAllSorted(Sort sort) {
        return lock.withReadLock(() -> original.findAllSorted(sort));
    }
}
