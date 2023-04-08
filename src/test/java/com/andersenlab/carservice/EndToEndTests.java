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
                """);

        Main.main(ARGS);

        assertThat(output)
                .hasToString("""
                        repairers add (id?, name) - add a repairer with given name and, optionally, ID
                        repairers list (sort) - list all known repairers sorted
                        repairers delete (id) - delete a repairer with given ID
                        garage-slots add (id?) - add a garage slot with, optionally, given ID
                        garage-slots list (sort) - list all known garage slots sorted
                        garage-slots delete (id) - delete a garage slot with given ID
                        orders create (id?, price) - create an order with given price and, optionally, ID
                        orders list (sort) - list all known orders sorted
                        help - print all available commands
                        """);
    }

    @Test
    void addRepairerWithoutId() {
        input("""
                repairers add John
                exit
                """);

        Main.main(ARGS);

        assertThat(output.toString()).contains("Repairer added");
    }

    @Test
    void addRepairerWithId(UUID repairerId1) {
        input("""
                repairers add %s John
                exit
                """
                .formatted(repairerId1)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Repairer added " + repairerId1);
    }

    @Test
    void listRepairers(UUID repairerId1) {
        input("""
                repairers add %s John
                repairers list name
                exit
                """
                .formatted(repairerId1)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Repairer added " + repairerId1, "1) " + repairerId1 + ", John");
    }

    @Test
    void deleteRepairerById(UUID repairerId1) {
        input("""
                repairers add %s John
                repairers delete %s
                exit
                """
                .formatted(repairerId1, repairerId1)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Repairer deleted");
    }

    @Test
    void addGarageSlotWithoutId() {
        input("""
                garage-slots add
                exit
                """);

        Main.main(ARGS);

        assertThat(output.toString()).contains("Garage slot added");
    }

    @Test
    void addGarageSlotWithId(UUID garageSlotId1) {
        input("""
                garage-slots add %s
                exit
                """
                .formatted(garageSlotId1)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Garage slot added " + garageSlotId1);
    }

    @Test
    void listGarageSlots(UUID garageSlotId1) {
        input("""
                garage-slots add %s
                garage-slots list id
                exit
                """
                .formatted(garageSlotId1)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Garage slot added " + garageSlotId1, "1) " + garageSlotId1);
    }

    @Test
    void deleteGarageSlotById(UUID garageSlotId1) {
        input("""
                garage-slots add %s
                garage-slots delete %s
                exit
                """
                .formatted(garageSlotId1, garageSlotId1)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Garage slot deleted");
    }

    @Test
    void createOrderWithoutId() {
        input("""
                orders create 100
                exit
                """);

        Main.main(ARGS);

        assertThat(output.toString()).contains("Order created");
    }

    @Test
    void createOrderWithId(UUID orderId1) {
        input("""
                orders create %s 100
                exit
                """
                .formatted(orderId1)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains("Order created " + orderId1);
    }

    @Test
    void listOrders(UUID orderId1) {
        input("""
                orders create %s 100
                orders list id
                exit
                """
                .formatted(orderId1)
        );

        Main.main(ARGS);

        assertThat(output.toString()).contains(
                "Order created " + orderId1,
                "1) " + orderId1 + ", 100, IN_PROCESS, ",
                ", NONE"
        );
    }

    private void input(String commands) {
        System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
    }
}
