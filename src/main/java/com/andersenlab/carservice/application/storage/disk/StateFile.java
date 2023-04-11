package com.andersenlab.carservice.application.storage.disk;

import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.external.RepairerStore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

final class StateFile {

    private final Path path;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    StateFile(Path path) {
        this.path = path;
    }

    State read() {
        try {
            return objectMapper.readValue(Files.newBufferedReader(path), State.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    record State(
            Collection<GarageSlotStore.GarageSlotEntity> garageSlots,
            Collection<OrderStore.OrderEntity> orders,
            Collection<RepairerStore.RepairerEntity> repairers
    ) {
        State(
                Collection<GarageSlotStore.GarageSlotEntity> garageSlots,
                Collection<OrderStore.OrderEntity> orders,
                Collection<RepairerStore.RepairerEntity> repairers
        ) {
            this.garageSlots = List.copyOf(garageSlots);
            this.orders = List.copyOf(orders);
            this.repairers = List.copyOf(repairers);
        }
    }
}
