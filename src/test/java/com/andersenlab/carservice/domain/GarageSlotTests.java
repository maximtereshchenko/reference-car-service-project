package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.CarServiceExtension;
import com.andersenlab.carservice.port.usecase.ListGarageSlotsUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(CarServiceExtension.class)
class GarageSlotTests {

    private final UUID firstGarageSlotId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void givenNoGarageSlots_whenAddGarageSlot_thenItShouldBeListed(CarServiceModule module) {
        module.addGarageSlotUseCase().add(firstGarageSlotId);

        assertThat(module.listGarageSlotsUseCase().list(ListGarageSlotsUseCase.Sort.ID))
                .containsExactly(new ListGarageSlotsUseCase.GarageSlotView(firstGarageSlotId));
    }
}
