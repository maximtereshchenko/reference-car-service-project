package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.application.InMemoryGarageSlotStore;
import com.andersenlab.carservice.application.InMemoryRepairerStore;
import com.andersenlab.carservice.port.usecase.ListGarageSlotsUseCase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GarageSlotTests {

    private final CarServiceModule module = new CarServiceModule(new InMemoryRepairerStore(), new InMemoryGarageSlotStore());
    private final UUID firstGarageSlotId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void givenNoGarageSlots_whenAddGarageSlot_thenItShouldBeListed() {
        module.addGarageSlotUseCase().add(firstGarageSlotId);

        assertThat(module.listGarageSlotsUseCase().list(ListGarageSlotsUseCase.Sort.ID))
                .containsExactly(new ListGarageSlotsUseCase.GarageSlotView(firstGarageSlotId));
    }
}
