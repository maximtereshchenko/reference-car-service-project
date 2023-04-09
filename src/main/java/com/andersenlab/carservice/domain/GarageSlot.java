package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.usecase.ListGarageSlotsUseCase;

import java.util.UUID;

final class GarageSlot {

    private final GarageSlotStore.GarageSlotEntity entity;

    GarageSlot(GarageSlotStore.GarageSlotEntity entity) {
        this.entity = entity;
    }

    GarageSlot(UUID id) {
        this(new GarageSlotStore.GarageSlotEntity(id, GarageSlotStore.GarageSlotStatus.AVAILABLE));
    }

    GarageSlotStore.GarageSlotEntity entity() {
        return entity;
    }

    ListGarageSlotsUseCase.GarageSlotView view() {
        return new ListGarageSlotsUseCase.GarageSlotView(
                entity.id(),
                ListGarageSlotsUseCase.GarageSlotStatus.valueOf(entity.status().name())
        );
    }

    GarageSlot asAssigned() {
        return new GarageSlot(
                new GarageSlotStore.GarageSlotEntity(
                        entity.id(),
                        GarageSlotStore.GarageSlotStatus.ASSIGNED
                )
        );
    }

    GarageSlot asAvailable() {
        return new GarageSlot(
                new GarageSlotStore.GarageSlotEntity(
                        entity.id(),
                        GarageSlotStore.GarageSlotStatus.AVAILABLE
                )
        );
    }
}
