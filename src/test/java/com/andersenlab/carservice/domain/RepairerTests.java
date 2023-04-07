package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.application.InMemoryRepairerStore;
import com.andersenlab.carservice.port.usecase.ListRepairersUserCase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RepairerTests {

    private final CarServiceModule module = new CarServiceModule(new InMemoryRepairerStore());
    private final UUID firstRepairerId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID secondRepairerId = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test
    void givenNoRepairers_whenAddRepairer_thenItShouldBeListed() {
        module.addRepairerUseCase().add(firstRepairerId, "John");

        assertThat(module.listRepairersUserCase().list(ListRepairersUserCase.Sort.NAME))
                .containsExactly(new ListRepairersUserCase.RepairerView(firstRepairerId, "John"));
    }

    @Test
    void givenSomeRepairers_whenListByName_thenTheyShouldBeSortedByName() {
        module.addRepairerUseCase().add(firstRepairerId, "John");
        module.addRepairerUseCase().add(secondRepairerId, "Andrei");

        assertThat(module.listRepairersUserCase().list(ListRepairersUserCase.Sort.NAME))
                .containsExactly(
                        new ListRepairersUserCase.RepairerView(secondRepairerId, "Andrei"),
                        new ListRepairersUserCase.RepairerView(firstRepairerId, "John")
                );
    }

    @Test
    void givenSomeRepairers_whenListById_thenTheyShouldBeSortedById() {
        module.addRepairerUseCase().add(secondRepairerId, "Andrei");
        module.addRepairerUseCase().add(firstRepairerId, "John");

        assertThat(module.listRepairersUserCase().list(ListRepairersUserCase.Sort.ID))
                .containsExactly(
                        new ListRepairersUserCase.RepairerView(firstRepairerId, "John"),
                        new ListRepairersUserCase.RepairerView(secondRepairerId, "Andrei")
                );
    }
}
