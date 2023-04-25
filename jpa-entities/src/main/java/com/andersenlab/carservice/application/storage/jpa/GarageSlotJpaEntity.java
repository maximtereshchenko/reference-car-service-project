package com.andersenlab.carservice.application.storage.jpa;

import com.andersenlab.carservice.port.external.GarageSlotStore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "garage_slots")
public class GarageSlotJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    protected GarageSlotJpaEntity() {
    }

    public GarageSlotJpaEntity(GarageSlotStore.GarageSlotEntity garageSlotEntity) {
        id = garageSlotEntity.id();
        status = garageSlotEntity.status().name();
        isDeleted = false;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Optional<GarageSlotStore.GarageSlotEntity> garageSlotEntity() {
        if (isDeleted) {
            return Optional.empty();
        }
        return Optional.of(
                new GarageSlotStore.GarageSlotEntity(
                        id,
                        GarageSlotStore.GarageSlotStatus.valueOf(status)
                )
        );
    }
}
