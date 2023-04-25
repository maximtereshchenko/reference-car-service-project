package com.andersenlab.carservice.application.storage.spring.jpa;

import com.andersenlab.carservice.application.storage.jpa.OrderJpaEntity;
import com.andersenlab.carservice.port.external.OrderStore;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

final class SpringDataJpaOrderStore implements OrderStore {

    private final OrderJpaEntityRepository repository;

    SpringDataJpaOrderStore(OrderJpaEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(OrderEntity orderEntity) {
        var entity = repository.findById(orderEntity.id())
                .orElseGet(() -> new OrderJpaEntity(orderEntity));
        entity.update(orderEntity);
        repository.save(entity);
    }

    @Override
    public Optional<OrderEntity> findById(UUID id) {
        return repository.findById(id)
                .map(OrderJpaEntity::orderEntity);
    }

    @Override
    public Collection<OrderProjection> findAllSorted(Sort sort) {
        return repository.findAll(
                        org.springframework.data.domain.Sort.by(sort.name().toLowerCase(Locale.ROOT))
                )
                .stream()
                .map(OrderJpaEntity::orderProjection)
                .toList();
    }
}
