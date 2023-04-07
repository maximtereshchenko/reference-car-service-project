package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.RepairerStore;
import com.andersenlab.carservice.port.usecase.AddRepairerUseCase;
import com.andersenlab.carservice.port.usecase.ListRepairersUserCase;

public final class CarServiceModule {

    private final RepairerService repairerService;

    public CarServiceModule(RepairerStore repairerStore) {
        repairerService = new RepairerService(repairerStore);
    }

    public AddRepairerUseCase addRepairerUseCase() {
        return repairerService;
    }

    public ListRepairersUserCase listRepairersUserCase() {
        return repairerService;
    }
}
