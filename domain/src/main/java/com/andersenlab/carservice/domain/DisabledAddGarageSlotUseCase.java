package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.usecase.AddGarageSlotUseCase;
import com.andersenlab.carservice.port.usecase.exception.GarageSlotAdditionIsDisabled;

import java.util.UUID;

final class DisabledAddGarageSlotUseCase implements AddGarageSlotUseCase {

    @Override
    public void add(UUID id) {
        throw new GarageSlotAdditionIsDisabled();
    }
}
