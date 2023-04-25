package com.andersenlab.carservice.application.storage.spring.jpa;

import com.andersenlab.carservice.application.storage.jpa.GarageSlotJpaEntity;
import com.andersenlab.carservice.port.external.GarageSlotStore;
import jakarta.persistence.EntityNotFoundException;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

final class SpringDataJpaGarageSlotStore implements GarageSlotStore {

    private final GarageSlotJpaEntityRepository repository;

    SpringDataJpaGarageSlotStore(GarageSlotJpaEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(GarageSlotEntity garageSlotEntity) {
        repository.findById(garageSlotEntity.id())
                .ifPresentOrElse(
                        garageSlotJpaEntity -> garageSlotJpaEntity.update(garageSlotEntity),
                        () -> repository.save(new GarageSlotJpaEntity(garageSlotEntity))
                );
    }

    @Override
    public Collection<GarageSlotEntity> findAllSorted(Sort sort) {
        return repository.findAll(
                        org.springframework.data.domain.Sort.by(sort.name().toLowerCase(Locale.ROOT))
                )
                .stream()
                .map(GarageSlotJpaEntity::garageSlotEntity)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        repository.findById(id)
                .ifPresent(GarageSlotJpaEntity::setDeleted);
    }

    @Override
    public boolean has(UUID id) {
        return repository.existsByIdAndIsDeletedFalse(id);
    }

    @Override
    public GarageSlotEntity getById(UUID id) {
        return repository.findById(id)
                .flatMap(GarageSlotJpaEntity::garageSlotEntity)
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    @Override
    public boolean hasGarageSlotWithStatusAssigned(UUID id) {
        return repository.existsByIdAndStatusAndIsDeletedFalse(id, GarageSlotStatus.ASSIGNED.name());
    }
}
