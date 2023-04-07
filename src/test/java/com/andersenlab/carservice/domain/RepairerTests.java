package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.application.InMemoryRepairerStore;
import com.andersenlab.carservice.port.usecase.ListRepairersUserCase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RepairerTests {

    private final CarServiceModule module = new CarServiceModule(new InMemoryRepairerStore());
    private final UUID repairerId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void givenNoRepairers_whenAddRepairer_thenItShouldBeListed() {
        module.addRepairerUseCase().add(repairerId, "John");

        assertThat(module.listRepairersUserCase().list())
                .containsExactly(new ListRepairersUserCase.RepairerView(repairerId, "John"));
    }
}
