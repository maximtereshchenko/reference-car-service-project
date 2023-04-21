package com.andersenlab.carservice.application.storage.jpa;

import com.andersenlab.carservice.port.external.RepairerStore;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public final class JpaRepairerStore implements RepairerStore {

    private final Database database;

    public JpaRepairerStore(Database database) {
        this.database = database;
    }

    @Override
    public void save(RepairerEntity repairerEntity) {
        if (has(repairerEntity.id())) {
            return;
        }
        database.persist(new RepairerJpaEntity(repairerEntity));
    }

    @Override
    public Collection<RepairerEntity> findAllSorted(Sort sort) {
        return database.query(RepairerJpaEntity.FIND_ALL_QUERY, RepairerJpaEntity.class)
                .stream()
                .map(RepairerJpaEntity::repairerEntity)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        database.findById(id, RepairerJpaEntity.class)
                .ifPresent(repairerJpaEntity -> repairerJpaEntity.setDeleted(true));
    }

    @Override
    public boolean has(UUID id) {
        return database.findById(id, RepairerJpaEntity.class)
                .flatMap(RepairerJpaEntity::repairerEntity)
                .isPresent();
    }

    @Override
    public boolean hasRepairerWithStatusAssigned(UUID id) {
        return database.findById(id, RepairerJpaEntity.class)
                .flatMap(RepairerJpaEntity::repairerEntity)
                .map(RepairerEntity::status)
                .filter(status -> status == RepairerStatus.ASSIGNED)
                .isPresent();
    }

    @Override
    public RepairerEntity getById(UUID id) {
        return database.findById(id, RepairerJpaEntity.class)
                .flatMap(RepairerJpaEntity::repairerEntity)
                .orElseThrow(() -> new CouldNotFindEntity(id));
    }
}
