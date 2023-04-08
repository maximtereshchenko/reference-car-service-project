package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.extension.CarServiceExtension;
import com.andersenlab.carservice.extension.PredictableUUIDExtension;
import com.andersenlab.carservice.port.usecase.ListGarageSlotsUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({CarServiceExtension.class, PredictableUUIDExtension.class})
class GarageSlotTests {

    @Test
    void givenNoGarageSlots_whenAddGarageSlot_thenItShouldBeListed(CarServiceModule module, UUID garageSlotId1) {
        module.addGarageSlotUseCase().add(garageSlotId1);

        assertThat(module.listGarageSlotsUseCase().list(ListGarageSlotsUseCase.Sort.ID))
                .containsExactly(new ListGarageSlotsUseCase.GarageSlotView(garageSlotId1));
    }

    @Test
    void givenOneGarageSlot_whenDeleteIt_thenNoGarageSlotsShouldBeSeen(CarServiceModule module, UUID garageSlotId1) {
        module.addGarageSlotUseCase().add(garageSlotId1);

        module.deleteGarageSlotUseCase().delete(garageSlotId1);

        assertThat(module.listGarageSlotsUseCase().list(ListGarageSlotsUseCase.Sort.ID)).isEmpty();
    }
}
