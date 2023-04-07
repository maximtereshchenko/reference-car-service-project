package com.andersenlab.carservce.domain;

import com.andersenlab.carservce.port.external.RepairerStore;
import com.andersenlab.carservce.port.usecase.AddRepairerUseCase;
import com.andersenlab.carservce.port.usecase.ListRepairersUserCase;

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
