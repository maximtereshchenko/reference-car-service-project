package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.application.InMemoryGarageSlotStore;
import com.andersenlab.carservice.application.InMemoryRepairerStore;
import com.andersenlab.carservice.port.usecase.ListRepairersUseCase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RepairerTests {

    private final CarServiceModule module = new CarServiceModule(new InMemoryRepairerStore(), new InMemoryGarageSlotStore());
    private final UUID firstRepairerId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID secondRepairerId = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test
    void givenNoRepairers_whenAddRepairer_thenItShouldBeListed() {
        module.addRepairerUseCase().add(firstRepairerId, "John");

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.NAME))
                .containsExactly(new ListRepairersUseCase.RepairerView(firstRepairerId, "John"));
    }

    @Test
    void givenSomeRepairers_whenListByName_thenTheyShouldBeSortedByName() {
        module.addRepairerUseCase().add(firstRepairerId, "John");
        module.addRepairerUseCase().add(secondRepairerId, "Andrei");

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.NAME))
                .containsExactly(
                        new ListRepairersUseCase.RepairerView(secondRepairerId, "Andrei"),
                        new ListRepairersUseCase.RepairerView(firstRepairerId, "John")
                );
    }

    @Test
    void givenSomeRepairers_whenListById_thenTheyShouldBeSortedById() {
        module.addRepairerUseCase().add(secondRepairerId, "Andrei");
        module.addRepairerUseCase().add(firstRepairerId, "John");

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.ID))
                .containsExactly(
                        new ListRepairersUseCase.RepairerView(firstRepairerId, "John"),
                        new ListRepairersUseCase.RepairerView(secondRepairerId, "Andrei")
                );
    }

    @Test
    void givenOneRepairer_whenDeleteHim_thenNoRepairersShouldBeSeen() {
        module.addRepairerUseCase().add(firstRepairerId, "John");

        module.deleteRepairerUseCase().delete(firstRepairerId);

        assertThat(module.listRepairersUserCase().list(ListRepairersUseCase.Sort.ID)).isEmpty();
    }
}
