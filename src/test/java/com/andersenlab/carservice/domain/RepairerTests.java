package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.CarServiceExtension;
import com.andersenlab.carservice.port.usecase.ListRepairersUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(CarServiceExtension.class)
class RepairerTests {

    private final UUID firstRepairerId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID secondRepairerId = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test
    void givenNoRepairers_whenAddRepairer_thenItShouldBeListed(CarServiceModule module) {
        module.addRepairerUseCase().add(firstRepairerId, "John");

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.NAME))
                .containsExactly(new ListRepairersUseCase.RepairerView(firstRepairerId, "John"));
    }

    @Test
    void givenSomeRepairers_whenListByName_thenTheyShouldBeSortedByName(CarServiceModule module) {
        module.addRepairerUseCase().add(firstRepairerId, "John");
        module.addRepairerUseCase().add(secondRepairerId, "Andrei");

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.NAME))
                .containsExactly(
                        new ListRepairersUseCase.RepairerView(secondRepairerId, "Andrei"),
                        new ListRepairersUseCase.RepairerView(firstRepairerId, "John")
                );
    }

    @Test
    void givenSomeRepairers_whenListById_thenTheyShouldBeSortedById(CarServiceModule module) {
        module.addRepairerUseCase().add(secondRepairerId, "Andrei");
        module.addRepairerUseCase().add(firstRepairerId, "John");

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.ID))
                .containsExactly(
                        new ListRepairersUseCase.RepairerView(firstRepairerId, "John"),
                        new ListRepairersUseCase.RepairerView(secondRepairerId, "Andrei")
                );
    }

    @Test
    void givenOneRepairer_whenDeleteHim_thenNoRepairersShouldBeSeen(CarServiceModule module) {
        module.addRepairerUseCase().add(firstRepairerId, "John");

        module.deleteRepairerUseCase().delete(firstRepairerId);

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.ID)).isEmpty();
    }
}
