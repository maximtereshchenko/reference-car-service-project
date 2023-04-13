package com.andersenlab.carservice.application.storage;

import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.external.RepairerStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public final class StateFile {

    private static final Logger LOG = LoggerFactory.getLogger(StateFile.class);

    private final Path path;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public StateFile(Path path) {
        this.path = path;
    }

    State read() {
        try (var reader = Files.newBufferedReader(path)) {
            return objectMapper.readValue(reader, State.class);
        } catch (IOException e) {
            LOG.warn("Could not read state", e);
            return new State();
        }
    }

    void write(State state) {
        try (var writer = Files.newBufferedWriter(path)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, state);
        } catch (IOException e) {
            throw new CanNotWriteState(e);
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

        State() {
            this(List.of(), List.of(), List.of());
        }

        State withGarageSlots(Collection<GarageSlotStore.GarageSlotEntity> garageSlots) {
            return new State(garageSlots, orders, repairers);
        }

        State withOrders(Collection<OrderStore.OrderEntity> orders) {
            return new State(garageSlots, orders, repairers);
        }

        State withRepairers(Collection<RepairerStore.RepairerEntity> repairers) {
            return new State(garageSlots, orders, repairers);
        }
    }

    public static final class CanNotWriteState extends RuntimeException {

        CanNotWriteState(Throwable cause) {
            super(cause);
        }
    }
}
