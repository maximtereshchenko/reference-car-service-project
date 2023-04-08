package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.external.RepairerStore;
import com.andersenlab.carservice.port.usecase.*;

public final class CarServiceModule {

    private final RepairerService repairerService;
    private final GarageSlotService garageSlotService;

    public CarServiceModule(RepairerStore repairerStore, GarageSlotStore garageSlotStore) {
        repairerService = new RepairerService(repairerStore);
        garageSlotService = new GarageSlotService(garageSlotStore);
    }

    public AddRepairerUseCase addRepairerUseCase() {
        return repairerService;
    }

    public ListRepairersUseCase listRepairersUserCase() {
        return repairerService;
    }

    public DeleteRepairerUseCase deleteRepairerUseCase() {
        return repairerService;
    }

    public AddGarageSlotUseCase addGarageSlotUseCase() {
        return garageSlotService;
    }

    public ListGarageSlotsUseCase listGarageSlotsUseCase() {
        return garageSlotService;
    }

    public DeleteGarageSlotUseCase deleteGarageSlotUseCase() {
        return garageSlotService;
    }
}
