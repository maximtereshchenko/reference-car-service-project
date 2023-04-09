package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.RepairerStore;
import com.andersenlab.carservice.port.usecase.ListRepairersUseCase;

import java.util.UUID;

final class Repairer {

    private final RepairerStore.RepairerEntity entity;

    Repairer(RepairerStore.RepairerEntity entity) {
        this.entity = entity;
    }

    Repairer(UUID id, String name) {
        this(new RepairerStore.RepairerEntity(id, name, RepairerStore.RepairerStatus.AVAILABLE));
    }

    ListRepairersUseCase.RepairerView view() {
        return new ListRepairersUseCase.RepairerView(
                entity.id(),
                entity.name(),
                ListRepairersUseCase.RepairerStatus.valueOf(entity.status().name())
        );
    }

    RepairerStore.RepairerEntity entity() {
        return entity;
    }

    Repairer asAssigned() {
        return new Repairer(
                new RepairerStore.RepairerEntity(
                        entity.id(),
                        entity.name(),
                        RepairerStore.RepairerStatus.ASSIGNED
                )
        );
    }
}
