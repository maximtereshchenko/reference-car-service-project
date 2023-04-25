package com.andersenlab.carservice.application.storage.jpa;

import com.andersenlab.carservice.port.external.GarageSlotStore;
import jakarta.persistence.EntityNotFoundException;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public final class JpaGarageSlotStore implements GarageSlotStore {

    private final Database database;

    public JpaGarageSlotStore(Database database) {
        this.database = database;
    }

    @Override
    public void save(GarageSlotEntity garageSlotEntity) {
        if (has(garageSlotEntity.id())) {
            return;
        }
        database.persist(new GarageSlotJpaEntity(garageSlotEntity));
    }

    @Override
    public Collection<GarageSlotEntity> findAllSorted(Sort sort) {
        return database.query(findAllQuery(sort), GarageSlotJpaEntity.class)
                .stream()
                .map(GarageSlotJpaEntity::garageSlotEntity)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        database.findById(id, GarageSlotJpaEntity.class)
                .ifPresent(GarageSlotJpaEntity::setDeleted);
    }

    @Override
    public boolean has(UUID id) {
        return database.findById(id, GarageSlotJpaEntity.class)
                .flatMap(GarageSlotJpaEntity::garageSlotEntity)
                .isPresent();
    }

    @Override
    public GarageSlotEntity getById(UUID id) {
        return database.findById(id, GarageSlotJpaEntity.class)
                .flatMap(GarageSlotJpaEntity::garageSlotEntity)
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    @Override
    public boolean hasGarageSlotWithStatusAssigned(UUID id) {
        return database.findById(id, GarageSlotJpaEntity.class)
                .flatMap(GarageSlotJpaEntity::garageSlotEntity)
                .map(GarageSlotEntity::status)
                .filter(status -> status == GarageSlotStatus.ASSIGNED)
                .isPresent();
    }

    private String findAllQuery(Sort sort) {
        return "select g from GarageSlotJpaEntity g where g.isDeleted = false order by g." +
                sort.name().toLowerCase(Locale.ROOT);
    }
}
