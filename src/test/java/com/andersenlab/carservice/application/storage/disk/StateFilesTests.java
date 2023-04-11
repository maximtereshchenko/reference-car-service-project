package com.andersenlab.carservice.application.storage.disk;

import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.external.RepairerStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

final class StateFilesTests {

    @Test
    void givenFileExists_whenRead_thenExpectedStateReturned() {
        var stateFile = new StateFile(Paths.get("src/test/resources/state.json"));

        assertThat(stateFile.read()).isEqualTo(state());
    }

    @Test
    void givenFileDoNotExist_whenRead_thenEmptyStateReturned() {
        var stateFile = new StateFile(Paths.get("non-existent-file.json"));

        assertThat(stateFile.read()).isEqualTo(new StateFile.State());
    }

    @Test
    void givenState_whenWrite_thenFileHasExpectedContent(@TempDir Path directory) {
        var actualStateFile = directory.resolve("actual.json");
        var stateFile = new StateFile(actualStateFile);

        stateFile.write(state());

        assertThat(actualStateFile)
                .hasSameTextualContentAs(Paths.get("src/test/resources/state.json"));
    }

    private StateFile.State state() {
        return new StateFile.State(
                List.of(
                        new GarageSlotStore.GarageSlotEntity(
                                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                                GarageSlotStore.GarageSlotStatus.AVAILABLE
                        )
                ),
                List.of(
                        new OrderStore.OrderEntity(
                                UUID.fromString("00000000-0000-0000-0000-000000000002"),
                                100,
                                OrderStore.OrderStatus.IN_PROCESS,
                                Optional.empty(),
                                Set.of(),
                                Instant.parse("2000-01-01T00:00:00.00Z"),
                                Optional.empty()
                        )
                ),
                List.of(
                        new RepairerStore.RepairerEntity(
                                UUID.fromString("00000000-0000-0000-0000-000000000003"),
                                "John",
                                RepairerStore.RepairerStatus.AVAILABLE
                        )
                )
        );
    }
}
