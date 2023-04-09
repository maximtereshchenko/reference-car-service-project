package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.usecase.AddGarageSlotUseCase;
import com.andersenlab.carservice.port.usecase.DeleteGarageSlotUseCase;
import com.andersenlab.carservice.port.usecase.ListGarageSlotsUseCase;

import java.util.List;
import java.util.UUID;

final class GarageSlotService implements AddGarageSlotUseCase, ListGarageSlotsUseCase, DeleteGarageSlotUseCase {

    private final GarageSlotStore garageSlotStore;

    GarageSlotService(GarageSlotStore garageSlotStore) {
        this.garageSlotStore = garageSlotStore;
    }

    @Override
    public void add(UUID id) {
        garageSlotStore.save(new GarageSlotStore.GarageSlotEntity(id));
    }

    @Override
    public List<GarageSlotView> list(Sort sort) {
        return garageSlotStore.findAllSorted(GarageSlotStore.Sort.valueOf(sort.name()))
                .stream()
                .map(garageSlotEntity -> new GarageSlotView(garageSlotEntity.id()))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        garageSlotStore.delete(id);
    }

    boolean hasNotGarageSlot(UUID id) {
        return garageSlotStore.hasNot(id);
    }
}
