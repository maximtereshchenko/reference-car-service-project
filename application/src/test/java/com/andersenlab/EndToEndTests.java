package com.andersenlab;

import com.andersenlab.carservice.port.usecase.*;
import com.andersenlab.extension.JettyExtension;
import com.andersenlab.extension.JsonHttpClient;
import com.andersenlab.extension.PredictableUUIDExtension;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({PredictableUUIDExtension.class, JettyExtension.class})
final class EndToEndTests {

    @Test
    @Order(0)
    void createRepairer(UUID repairerId, JsonHttpClient client) throws Exception {
        var response = client.post(
                "/repairers",
                Map.of(
                        "id", repairerId.toString(),
                        "name", "John"
                ),
                new TypeReference<UUID>() {
                }
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo(repairerId);
    }

    @Test
    @Order(1)
    void createRepairerWithSameId(UUID repairerId, JsonHttpClient client) throws Exception {
        var response = client.post(
                "/repairers",
                Map.of(
                        "id", repairerId.toString(),
                        "name", "John"
                )
        );

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    @Order(2)
    void listRepairers(UUID repairerId, JsonHttpClient client) throws Exception {
        var response = client.get(
                "/repairers?sort=id",
                new TypeReference<Collection<ListRepairersUseCase.RepairerView>>() {}
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .singleElement()
                .isEqualTo(
                        new ListRepairersUseCase.RepairerView(
                                repairerId,
                                "John",
                                ListRepairersUseCase.RepairerStatus.AVAILABLE
                        )
                );
    }

    @Test
    @Order(3)
    void createGarageSlot(UUID garageSlot, JsonHttpClient client) throws Exception {
        var response = client.post(
                "/garage-slots",
                Map.of("id", garageSlot.toString()),
                new TypeReference<UUID>() {}
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo(garageSlot);
    }

    @Test
    @Order(4)
    void listGarageSlots(UUID garageSlot, JsonHttpClient client) throws Exception {
        var response = client.get(
                "/garage-slots?sort=id",
                new TypeReference<Collection<ListGarageSlotsUseCase.GarageSlotView>>() {}
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .singleElement()
                .isEqualTo(
                        new ListGarageSlotsUseCase.GarageSlotView(
                                garageSlot,
                                ListGarageSlotsUseCase.GarageSlotStatus.AVAILABLE
                        )
                );
    }

    @Test
    @Order(5)
    void createOrder(UUID orderId1, JsonHttpClient client) throws Exception {
        var response = client.post(
                "/orders",
                Map.of(
                        "id", orderId1.toString(),
                        "price", "100"
                ),
                new TypeReference<UUID>() {}
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo(orderId1);
    }

    @Test
    @Order(6)
    void cancelOrder(UUID orderId1, JsonHttpClient client) throws Exception {
        var response = client.post("/orders/cancel", Map.of("id", orderId1.toString()));

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(7)
    void assignGarageSlot(UUID orderId2, UUID garageSlot, JsonHttpClient client) throws Exception {
        client.post(
                "/orders",
                Map.of(
                        "id", orderId2.toString(),
                        "price", "100"
                )
        );
        var response = client.post(
                "/orders/assign/garage-slot",
                Map.of(
                        "id", orderId2.toString(),
                        "garageSlotId", garageSlot.toString()
                )
        );

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(8)
    void assignRepairer(UUID orderId2, UUID repairerId, JsonHttpClient client) throws Exception {
        var response = client.post(
                "/orders/assign/repairer",
                Map.of(
                        "id", orderId2.toString(),
                        "repairerId", repairerId.toString()
                )
        );

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(9)
    void completeOrder(UUID orderId2, JsonHttpClient client) throws Exception {
        var response = client.post(
                "/orders/complete",
                Map.of("id", orderId2.toString())
        );

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(10)
    void viewOrder(UUID orderId2, UUID garageSlotId, UUID repairerId, Instant timestamp, JsonHttpClient client)
            throws Exception {
        var response = client.get(
                "/orders/" + orderId2,
                new TypeReference<ViewOrderUseCase.OrderView>() {}
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .isEqualTo(
                        new ViewOrderUseCase.OrderView(
                                orderId2,
                                100,
                                OrderStatus.COMPLETED,
                                Optional.of(garageSlotId),
                                Set.of(repairerId),
                                timestamp,
                                Optional.of(timestamp)
                        )
                );
    }

    @Test
    @Order(11)
    void listOrders(UUID orderId1, UUID orderId2, Instant timestamp, JsonHttpClient client) throws Exception {
        var response = client.get(
                "/orders?sort=id",
                new TypeReference<Collection<ListOrdersUseCase.OrderView>>() {}
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .containsExactly(
                        new ListOrdersUseCase.OrderView(
                                orderId1,
                                100,
                                OrderStatus.CANCELED,
                                timestamp,
                                Optional.of(timestamp)
                        ),
                        new ListOrdersUseCase.OrderView(
                                orderId2,
                                100,
                                OrderStatus.COMPLETED,
                                timestamp,
                                Optional.of(timestamp)
                        )
                );
    }

    @Test
    @Order(12)
    void deleteRepairer(UUID repairerId, JsonHttpClient client) throws Exception {
        var response = client.delete("/repairers/" + repairerId);

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    @Order(13)
    void deleteGarageSlot(UUID garageSlotId, JsonHttpClient client) throws Exception {
        var response = client.delete("/garage-slots/" + garageSlotId);

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    @Order(14)
    void listOrdersWithoutSortParameter(JsonHttpClient client) throws Exception {
        var response = client.get("/orders");

        assertThat(response.statusCode()).isEqualTo(400);
    }
}
