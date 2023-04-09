package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.extension.CarServiceExtension;
import com.andersenlab.carservice.extension.PredictableUUIDExtension;
import com.andersenlab.carservice.port.usecase.ListRepairersUseCase;
import com.andersenlab.carservice.port.usecase.exception.RepairerIsAssigned;
import com.andersenlab.carservice.port.usecase.exception.RepairerWithSameIdExists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({CarServiceExtension.class, PredictableUUIDExtension.class})
class RepairerTests {

    @Test
    void givenNoRepairers_whenAddRepairer_thenItShouldBeListed(CarServiceModule module, UUID repairerId) {
        module.addRepairerUseCase().add(repairerId, "John");

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.NAME))
                .containsExactly(
                        new ListRepairersUseCase.RepairerView(
                                repairerId,
                                "John",
                                ListRepairersUseCase.RepairerStatus.AVAILABLE
                        )
                );
    }

    @Test
    void givenRepairerExists_whenAddRepairer_thenRepairerWithSameIdExistsThrown(CarServiceModule module, UUID repairerId) {
        var useCase = module.addRepairerUseCase();
        useCase.add(repairerId, "John");

        assertThatThrownBy(() -> useCase.add(repairerId, "Andrei")).isInstanceOf(RepairerWithSameIdExists.class);
    }

    @Test
    void givenSomeRepairers_whenListByName_thenTheyShouldBeSortedByName(
            CarServiceModule module,
            UUID repairerId1,
            UUID repairerId2
    ) {
        module.addRepairerUseCase().add(repairerId1, "John");
        module.addRepairerUseCase().add(repairerId2, "Andrei");

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.NAME))
                .containsExactly(
                        new ListRepairersUseCase.RepairerView(
                                repairerId2,
                                "Andrei",
                                ListRepairersUseCase.RepairerStatus.AVAILABLE
                        ),
                        new ListRepairersUseCase.RepairerView(
                                repairerId1,
                                "John",
                                ListRepairersUseCase.RepairerStatus.AVAILABLE
                        )
                );
    }

    @Test
    void givenSomeRepairers_whenListById_thenTheyShouldBeSortedById(
            CarServiceModule module,
            UUID repairerId1,
            UUID repairerId2
    ) {
        module.addRepairerUseCase().add(repairerId2, "Andrei");
        module.addRepairerUseCase().add(repairerId1, "John");

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.ID))
                .containsExactly(
                        new ListRepairersUseCase.RepairerView(
                                repairerId1,
                                "John",
                                ListRepairersUseCase.RepairerStatus.AVAILABLE
                        ),
                        new ListRepairersUseCase.RepairerView(
                                repairerId2,
                                "Andrei",
                                ListRepairersUseCase.RepairerStatus.AVAILABLE
                        )
                );
    }

    @Test
    void givenSomeRepairers_whenListByStatus_thenTheyShouldBeSortedByStatus(
            CarServiceModule module,
            UUID repairerId1,
            UUID repairerId2,
            UUID orderId
    ) {
        module.addRepairerUseCase().add(repairerId1, "John");
        module.addRepairerUseCase().add(repairerId2, "Andrei");
        module.createOrderUseCase().create(orderId, 100);
        module.assignRepairerToOrderUseCase().assignRepairer(orderId, repairerId1);

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.STATUS))
                .containsExactly(
                        new ListRepairersUseCase.RepairerView(
                                repairerId2,
                                "Andrei",
                                ListRepairersUseCase.RepairerStatus.AVAILABLE
                        ),
                        new ListRepairersUseCase.RepairerView(
                                repairerId1,
                                "John",
                                ListRepairersUseCase.RepairerStatus.ASSIGNED
                        )
                );
    }

    @Test
    void givenOneRepairer_whenDeleteHim_thenNoRepairersShouldBeSeen(CarServiceModule module, UUID repairerId) {
        module.addRepairerUseCase().add(repairerId, "John");

        module.deleteRepairerUseCase().delete(repairerId);

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.ID)).isEmpty();
    }

    @Test
    void givenRepairerAssignedToProcessingOrder_whenDeleteRepairer_thenRepairerIsAssignedThrown(
            CarServiceModule module,
            UUID repairerId,
            UUID orderId
    ) {
        module.addRepairerUseCase().add(repairerId, "John");
        module.createOrderUseCase().create(orderId, 100);
        module.assignRepairerToOrderUseCase().assignRepairer(orderId, repairerId);
        var useCase = module.deleteRepairerUseCase();

        assertThatThrownBy(() -> useCase.delete(repairerId)).isInstanceOf(RepairerIsAssigned.class);
    }

    @Test
    void givenRepairerAssignedToProcessingOrder_whenCompleteOrder_thenRepairerShouldBeAvailableAgain(
            CarServiceModule module,
            UUID repairerId,
            UUID garageId,
            UUID orderId
    ) {
        module.addRepairerUseCase().add(repairerId, "John");
        module.addGarageSlotUseCase().add(garageId);
        module.createOrderUseCase().create(orderId, 100);
        module.assignRepairerToOrderUseCase().assignRepairer(orderId, repairerId);
        module.assignGarageSlotToOrderUseCase().assignGarageSlot(orderId, garageId);

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.STATUS))
                .containsExactly(
                        new ListRepairersUseCase.RepairerView(
                                repairerId,
                                "John",
                                ListRepairersUseCase.RepairerStatus.ASSIGNED
                        )
                );

        module.completeOrderUseCase().complete(orderId);

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.STATUS))
                .containsExactly(
                        new ListRepairersUseCase.RepairerView(
                                repairerId,
                                "John",
                                ListRepairersUseCase.RepairerStatus.AVAILABLE
                        )
                );
    }
}
