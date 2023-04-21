package com.andersenlab.carservice.application.storage.jpa;

import com.andersenlab.carservice.port.external.RepairerStore;
import jakarta.persistence.*;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "repairers")
@NamedQuery(name = RepairerJpaEntity.FIND_ALL_QUERY, query = "select r from RepairerJpaEntity r")
class RepairerJpaEntity {

    static final String FIND_ALL_QUERY = "findAll";

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    protected RepairerJpaEntity() {
    }

    RepairerJpaEntity(RepairerStore.RepairerEntity repairerEntity) {
        id = repairerEntity.id();
        name = repairerEntity.name();
        status = repairerEntity.status().name();
        isDeleted = false;
    }

    void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    Optional<RepairerStore.RepairerEntity> repairerEntity() {
        if (isDeleted) {
            return Optional.empty();
        }
        return Optional.of(
                new RepairerStore.RepairerEntity(
                        id,
                        name,
                        RepairerStore.RepairerStatus.valueOf(status)
                )
        );
    }
}
