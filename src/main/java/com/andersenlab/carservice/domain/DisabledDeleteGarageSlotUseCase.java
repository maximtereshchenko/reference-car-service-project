package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.usecase.DeleteGarageSlotUseCase;
import com.andersenlab.carservice.port.usecase.exception.GarageSlotDeletionIsDisabled;

import java.util.UUID;

final class DisabledDeleteGarageSlotUseCase implements DeleteGarageSlotUseCase {

    @Override
    public void delete(UUID id) {
        throw new GarageSlotDeletionIsDisabled();
    }
}
