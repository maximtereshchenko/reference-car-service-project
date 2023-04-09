package com.andersenlab.carservice;

import com.andersenlab.carservice.extension.PredictableUUIDExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith(PredictableUUIDExtension.class)
final class EndToEndTests {

    private static final String[] ARGS = {};

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @Test
    void help() {
        input("""
                help
                exit
                """
        );

        Main.main(ARGS);

        var printed = output.toString();
        assertThat(printed.lines().count()).isGreaterThan(1);
        assertThat(printed).contains("help - print all available commands");
    }

    @Test
    void addRepairerWithoutId() {
        input("""
                repairers add John
                exit
                """
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Repairer added");
    }

    @Test
    void addRepairerWithId(UUID repairerId) {
        input("""
                repairers add %s John
                exit
                """
                .formatted(repairerId)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Repairer added " + repairerId);
    }

    @Test
    void addRepairerWithNonUUIDId() {
        input(
                """
                        repairers add this-is-not-uuid John
                        exit
                        """
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Wrong arguments");
    }

    @Test
    void listRepairers(UUID repairerId) {
        input("""
                repairers add %s John
                repairers list name
                exit
                """
                .formatted(repairerId)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Repairer added " + repairerId, "1) " + repairerId + ", John");
    }

    @Test
    void listRepairersWithNotEnoughArguments() {
        input(
                """
                        repairers list
                        exit
                        """
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Wrong arguments");
    }

    @Test
    void deleteRepairerById(UUID repairerId) {
        input("""
                repairers add %s John
                repairers delete %s
                exit
                """
                .formatted(repairerId, repairerId)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Repairer deleted");
    }

    @Test
    void addGarageSlotWithoutId() {
        input("""
                garage-slots add
                exit
                """
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Garage slot added");
    }

    @Test
    void addGarageSlotWithId(UUID garageSlotId) {
        input("""
                garage-slots add %s
                exit
                """
                .formatted(garageSlotId)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Garage slot added " + garageSlotId);
    }

    @Test
    void listGarageSlots(UUID garageSlotId) {
        input("""
                garage-slots add %s
                garage-slots list id
                exit
                """
                .formatted(garageSlotId)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Garage slot added " + garageSlotId, "1) " + garageSlotId);
    }

    @Test
    void deleteGarageSlotById(UUID garageSlotId) {
        input("""
                garage-slots add %s
                garage-slots delete %s
                exit
                """
                .formatted(garageSlotId, garageSlotId)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Garage slot deleted");
    }

    @Test
    void createOrderWithoutId() {
        input("""
                orders create 100
                exit
                """
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Order created");
    }

    @Test
    void createOrderWithId(UUID orderId) {
        input("""
                orders create %s 100
                exit
                """
                .formatted(orderId)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Order created " + orderId);
    }

    @Test
    void listOrders(UUID orderId) {
        input("""
                orders create %s 100
                orders list id
                exit
                """
                .formatted(orderId)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains(
                "Order created " + orderId,
                "1) " + orderId + ", 100, IN_PROCESS, ",
                ", NONE"
        );
    }

    @Test
    void assignGarageSlotToOrder(UUID garageSlotId, UUID orderId) {
        input("""
                garage-slots add %s
                orders create %s 100
                orders assign garage-slot %s %s
                exit
                """
                .formatted(garageSlotId, orderId, orderId, garageSlotId)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Garage slot " + garageSlotId + " assigned to order " + orderId);
    }

    @Test
    void assignNonExistentGarageSlotToOrder(UUID garageSlotId, UUID orderId) {
        input("""
                orders create %s 100
                orders assign garage-slot %s %s
                exit
                """
                .formatted(orderId, orderId, garageSlotId)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Garage slot was not found");
    }

    @Test
    void viewOrder(UUID orderId) {
        input("""
                orders create %s 100
                orders view %s
                exit
                """
                .formatted(orderId, orderId)
        );

        Main.main(ARGS);

        assertThat(output.toString())
                .contains(
                        """
                                ID: %s
                                Price: 100
                                Status: IN_PROCESS
                                Garage slot: NONE
                                Repairers: NONE
                                Created:\
                                """
                                .formatted(orderId),
                        "Closed: NONE"
                );
    }

    @Test
    void assignRepairerToOrder(UUID repairerId, UUID orderId) {
        input("""
                repairers add %s John
                orders create %s 100
                orders assign repairer %s %s
                exit
                """
                .formatted(repairerId, orderId, orderId, repairerId)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Repairer " + repairerId + " assigned to order " + orderId);
    }

    @Test
    void completeOrder(UUID repairerId, UUID garageSlotId, UUID orderId) {
        input("""
                repairers add %s John
                garage-slots add %s
                orders create %s 100
                orders assign repairer %s %s
                orders assign garage-slot %s %s
                orders complete %s
                exit
                """
                .formatted(
                        repairerId,
                        garageSlotId,
                        orderId,
                        orderId, repairerId,
                        orderId, garageSlotId,
                        orderId
                )
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Order completed");
    }

    @Test
    void cancelOrder(UUID orderId) {
        input("""
                orders create %s 100
                orders cancel %s
                exit
                """
                .formatted(orderId, orderId)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Order canceled");
    }

    private void input(String commands) {
        System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
    }
}
