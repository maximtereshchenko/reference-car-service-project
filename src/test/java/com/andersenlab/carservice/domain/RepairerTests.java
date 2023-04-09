package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.extension.CarServiceExtension;
import com.andersenlab.carservice.extension.PredictableUUIDExtension;
import com.andersenlab.carservice.port.usecase.ListRepairersUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({CarServiceExtension.class, PredictableUUIDExtension.class})
class RepairerTests {

    @Test
    void givenNoRepairers_whenAddRepairer_thenItShouldBeListed(CarServiceModule module, UUID repairerId) {
        module.addRepairerUseCase().add(repairerId, "John");

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.NAME))
                .containsExactly(new ListRepairersUseCase.RepairerView(repairerId, "John"));
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
                        new ListRepairersUseCase.RepairerView(repairerId2, "Andrei"),
                        new ListRepairersUseCase.RepairerView(repairerId1, "John")
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
                        new ListRepairersUseCase.RepairerView(repairerId1, "John"),
                        new ListRepairersUseCase.RepairerView(repairerId2, "Andrei")
                );
    }

    @Test
    void givenOneRepairer_whenDeleteHim_thenNoRepairersShouldBeSeen(CarServiceModule module, UUID repairerId) {
        module.addRepairerUseCase().add(repairerId, "John");

        module.deleteRepairerUseCase().delete(repairerId);

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.ID)).isEmpty();
    }
}
