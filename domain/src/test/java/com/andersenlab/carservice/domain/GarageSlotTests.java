package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.usecase.ListGarageSlotsUseCase;
import com.andersenlab.carservice.port.usecase.exception.GarageSlotAdditionIsDisabled;
import com.andersenlab.carservice.port.usecase.exception.GarageSlotDeletionIsDisabled;
import com.andersenlab.carservice.port.usecase.exception.GarageSlotIsAssigned;
import com.andersenlab.carservice.port.usecase.exception.GarageSlotWithSameIdExists;
import com.andersenlab.extension.CarServiceExtension;
import com.andersenlab.extension.PredictableUUIDExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({CarServiceExtension.class, PredictableUUIDExtension.class})
class GarageSlotTests {

    @Test
    void givenNoGarageSlots_whenAddGarageSlot_thenItShouldBeListed(CarServiceModule module, UUID garageSlotId) {
        module.addGarageSlotUseCase().add(garageSlotId);

        assertThat(module.listGarageSlotsUseCase().list(ListGarageSlotsUseCase.Sort.ID))
                .containsExactly(
                        new ListGarageSlotsUseCase.GarageSlotView(
                                garageSlotId,
                                ListGarageSlotsUseCase.GarageSlotStatus.AVAILABLE
                        )
                );
    }

    @Test
    void givenGarageSlotExists_whenAddGarageSlot_thenGarageSlotWithSameIdExistsThrown(
            CarServiceModule module,
            UUID garageSlotId
    ) {
        var useCase = module.addGarageSlotUseCase();
        useCase.add(garageSlotId);

        assertThatThrownBy(() -> useCase.add(garageSlotId)).isInstanceOf(GarageSlotWithSameIdExists.class);
    }

    @Test
    void givenOneGarageSlot_whenDeleteIt_thenNoGarageSlotsShouldBeSeen(CarServiceModule module, UUID garageSlotId) {
        module.addGarageSlotUseCase().add(garageSlotId);

        module.deleteGarageSlotUseCase().delete(garageSlotId);

        assertThat(module.listGarageSlotsUseCase().list(ListGarageSlotsUseCase.Sort.ID)).isEmpty();
    }

    @Test
    void givenSomeGarageSlots_whenListGarageSlotsByStatus_thenTheyShouldBeSortedByStatus(
            CarServiceModule module,
            UUID garageSlotId1,
            UUID garageSlotId2,
            UUID orderId
    ) {
        module.addGarageSlotUseCase().add(garageSlotId1);
        module.addGarageSlotUseCase().add(garageSlotId2);
        module.createOrderUseCase().create(orderId, 100);
        module.assignGarageSlotToOrderUseCase().assignGarageSlot(orderId, garageSlotId1);

        assertThat(module.listGarageSlotsUseCase().list(ListGarageSlotsUseCase.Sort.STATUS))
                .containsExactly(
                        new ListGarageSlotsUseCase.GarageSlotView(
                                garageSlotId2,
                                ListGarageSlotsUseCase.GarageSlotStatus.AVAILABLE
                        ),
                        new ListGarageSlotsUseCase.GarageSlotView(
                                garageSlotId1,
                                ListGarageSlotsUseCase.GarageSlotStatus.ASSIGNED
                        )
                );
    }

    @Test
    void givenGarageSlotAssignedToOrder_whenDeleteIt_thenGarageSlotIsAssignedThrown(
            CarServiceModule module,
            UUID garageSlotId,
            UUID orderId
    ) {
        module.addGarageSlotUseCase().add(garageSlotId);
        module.createOrderUseCase().create(orderId, 100);
        module.assignGarageSlotToOrderUseCase().assignGarageSlot(orderId, garageSlotId);
        var useCase = module.deleteGarageSlotUseCase();

        assertThatThrownBy(() -> useCase.delete(garageSlotId)).isInstanceOf(GarageSlotIsAssigned.class);
    }

    @Test
    void givenGarageSlotAssignedToProcessingOrder_whenCompleteOrder_thenGarageSlotShouldBeAvailableAgain(
            CarServiceModule module,
            UUID garageId,
            UUID repairerId,
            UUID orderId
    ) {
        module.addGarageSlotUseCase().add(garageId);
        module.addRepairerUseCase().add(repairerId, "John");
        module.createOrderUseCase().create(orderId, 100);
        module.assignGarageSlotToOrderUseCase().assignGarageSlot(orderId, garageId);
        module.assignRepairerToOrderUseCase().assignRepairer(orderId, repairerId);

        assertThat(module.listGarageSlotsUseCase().list(ListGarageSlotsUseCase.Sort.ID))
                .containsExactly(
                        new ListGarageSlotsUseCase.GarageSlotView(
                                garageId,
                                ListGarageSlotsUseCase.GarageSlotStatus.ASSIGNED
                        )
                );

        module.completeOrderUseCase().complete(orderId);

        assertThat(module.listGarageSlotsUseCase().list(ListGarageSlotsUseCase.Sort.ID))
                .containsExactly(
                        new ListGarageSlotsUseCase.GarageSlotView(
                                garageId,
                                ListGarageSlotsUseCase.GarageSlotStatus.AVAILABLE
                        )
                );
    }

    @Test
    void givenGarageSlotAssignedToProcessingOrder_whenCancelOrder_thenGarageSlotShouldBeAvailableAgain(
            CarServiceModule module,
            UUID garageId,
            UUID orderId
    ) {
        module.addGarageSlotUseCase().add(garageId);
        module.createOrderUseCase().create(orderId, 100);
        module.assignGarageSlotToOrderUseCase().assignGarageSlot(orderId, garageId);

        assertThat(module.listGarageSlotsUseCase().list(ListGarageSlotsUseCase.Sort.ID))
                .containsExactly(
                        new ListGarageSlotsUseCase.GarageSlotView(
                                garageId,
                                ListGarageSlotsUseCase.GarageSlotStatus.ASSIGNED
                        )
                );

        module.cancelOrderUseCase().cancel(orderId);

        assertThat(module.listGarageSlotsUseCase().list(ListGarageSlotsUseCase.Sort.ID))
                .containsExactly(
                        new ListGarageSlotsUseCase.GarageSlotView(
                                garageId,
                                ListGarageSlotsUseCase.GarageSlotStatus.AVAILABLE
                        )
                );
    }

    @Test
    void givenGarageSlotAdditionIsDisabled_whenAddGarageSlot_thenGarageSlotAdditionIsDisabledThrown(
            Module.Builder builder,
            UUID garageSlotId
    ) {
        var module = builder.garageSlotAdditionEnabled(false).build();
        var useCase = module.addGarageSlotUseCase();

        assertThatThrownBy(() -> useCase.add(garageSlotId)).isInstanceOf(GarageSlotAdditionIsDisabled.class);
    }

    @Test
    void givenGarageSlotDeletionIsDisabled_whenDeleteGarageSlot_thenGarageSlotDeletionIsDisabledThrown(
            Module.Builder builder,
            UUID garageSlotId
    ) {
        var module = builder.garageSlotDeletionEnabled(false).build();
        var useCase = module.deleteGarageSlotUseCase();

        assertThatThrownBy(() -> useCase.delete(garageSlotId)).isInstanceOf(GarageSlotDeletionIsDisabled.class);
    }
}
