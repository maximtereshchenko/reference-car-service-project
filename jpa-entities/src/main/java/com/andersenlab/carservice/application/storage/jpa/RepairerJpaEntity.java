package com.andersenlab.carservice.application.storage.jpa;

import com.andersenlab.carservice.port.external.RepairerStore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "repairers")
public class RepairerJpaEntity {

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

    public RepairerJpaEntity(RepairerStore.RepairerEntity repairerEntity) {
        isDeleted = false;
        update(repairerEntity);
    }

    public Optional<RepairerStore.RepairerEntity> repairerEntity() {
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

    public void setDeleted() {
        isDeleted = true;
    }

    public void update(RepairerStore.RepairerEntity repairerEntity) {
        id = repairerEntity.id();
        name = repairerEntity.name();
        status = repairerEntity.status().name();
    }
}
