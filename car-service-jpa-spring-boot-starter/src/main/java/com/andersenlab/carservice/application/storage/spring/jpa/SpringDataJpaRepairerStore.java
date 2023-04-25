package com.andersenlab.carservice.application.storage.spring.jpa;

import com.andersenlab.carservice.application.storage.jpa.RepairerJpaEntity;
import com.andersenlab.carservice.port.external.RepairerStore;
import jakarta.persistence.EntityNotFoundException;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

final class SpringDataJpaRepairerStore implements RepairerStore {

    private final RepairerJpaEntityRepository repository;

    SpringDataJpaRepairerStore(RepairerJpaEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(RepairerEntity repairerEntity) {
        repository.findById(repairerEntity.id())
                .ifPresentOrElse(
                        repairerJpaEntity -> repairerJpaEntity.update(repairerEntity),
                        () -> repository.save(new RepairerJpaEntity(repairerEntity))
                );
    }

    @Override
    public Collection<RepairerEntity> findAllSorted(Sort sort) {
        return repository.findAll(
                        org.springframework.data.domain.Sort.by(sort.name().toLowerCase(Locale.ROOT))
                )
                .stream()
                .map(RepairerJpaEntity::repairerEntity)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        repository.findById(id)
                .ifPresent(RepairerJpaEntity::setDeleted);
    }

    @Override
    public boolean has(UUID id) {
        return repository.existsByIdAndIsDeletedFalse(id);
    }

    @Override
    public boolean hasRepairerWithStatusAssigned(UUID id) {
        return repository.existsByIdAndStatusAndIsDeletedFalse(id, RepairerStatus.ASSIGNED.name());
    }

    @Override
    public RepairerEntity getById(UUID id) {
        return repository.findById(id)
                .flatMap(RepairerJpaEntity::repairerEntity)
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }
}
