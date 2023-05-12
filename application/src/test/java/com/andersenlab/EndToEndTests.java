package com.andersenlab;

import com.andersenlab.carservice.port.usecase.*;
import com.andersenlab.extension.PredictableUUIDExtension;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EndToEndTests.TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({PredictableUUIDExtension.class, ContainersExtension.class})
final class EndToEndTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(50)
    void livenessProbeEnabled() {
        var response = restTemplate.getForEntity("/actuator/health/liveness", Health.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new Health());
    }

    @Test
    @Order(75)
    void readinessProbeEnabled() {
        var response = restTemplate.getForEntity("/actuator/health/readiness", Health.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new Health());
    }

    @Test
    @Order(100)
    void createRepairer(UUID repairerId) {
        var response = restTemplate.postForEntity(
                "/repairers",
                Map.of(
                        "id", repairerId.toString(),
                        "name", "John"
                ),
                UUID.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(repairerId);
    }

    @Test
    @Order(200)
    void createRepairerWithSameId(UUID repairerId) {
        var response = restTemplate.postForEntity(
                "/repairers",
                Map.of(
                        "id", repairerId.toString(),
                        "name", "John"
                ),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(300)
    void listRepairers(UUID repairerId) {
        var response = restTemplate.exchange(
                RequestEntity.get("/repairers?sort=id").build(),
                new ParameterizedTypeReference<Collection<ListRepairersUseCase.RepairerView>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
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
    @Order(400)
    void createGarageSlot(UUID garageSlot) {
        var response = restTemplate.postForEntity(
                "/garage-slots",
                Map.of("id", garageSlot.toString()),
                UUID.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(garageSlot);
    }

    @Test
    @Order(500)
    void listGarageSlots(UUID garageSlot) {
        var response = restTemplate.exchange(
                RequestEntity.get("/garage-slots?sort=id").build(),
                new ParameterizedTypeReference<Collection<ListGarageSlotsUseCase.GarageSlotView>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .singleElement()
                .isEqualTo(
                        new ListGarageSlotsUseCase.GarageSlotView(
                                garageSlot,
                                ListGarageSlotsUseCase.GarageSlotStatus.AVAILABLE
                        )
                );
    }

    @Test
    @Order(600)
    void createOrder(UUID orderId1) {
        var response = restTemplate.postForEntity(
                "/orders",
                Map.of(
                        "id", orderId1.toString(),
                        "price", "100"
                ),
                UUID.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(orderId1);
    }

    @Test
    @Order(700)
    void cancelOrder(UUID orderId1) {
        var response = restTemplate.postForEntity("/orders/{id}/cancel", null, Void.class, orderId1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(800)
    void assignGarageSlot(UUID orderId2, UUID garageSlotId) {
        restTemplate.postForEntity(
                "/orders",
                Map.of(
                        "id", orderId2.toString(),
                        "price", "100"
                ),
                Void.class
        );
        var response = restTemplate.postForEntity(
                "/orders/{id}/assign/garage-slot/{garageSlotId}",
                null,
                Void.class,
                orderId2,
                garageSlotId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(900)
    void assignRepairer(UUID orderId2, UUID repairerId) {
        var response = restTemplate.postForEntity(
                "/orders/{id}/assign/repairer/{repairerId}",
                null,
                Void.class,
                orderId2,
                repairerId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(1000)
    void completeOrder(UUID orderId2) {
        var response = restTemplate.postForEntity(
                "/orders/{id}/complete",
                null,
                Void.class,
                orderId2
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(1100)
    void viewOrder(UUID orderId2, UUID garageSlotId, UUID repairerId) {
        var response = restTemplate.getForEntity(
                "/orders/{id}",
                ViewOrderUseCase.OrderView.class,
                orderId2
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isEqualTo(
                        new ViewOrderUseCase.OrderView(
                                orderId2,
                                100,
                                OrderStatus.COMPLETED,
                                Optional.of(garageSlotId),
                                Set.of(repairerId),
                                TestConfig.TIMESTAMP,
                                Optional.of(TestConfig.TIMESTAMP)
                        )
                );
    }

    @Test
    @Order(1200)
    void listOrders(UUID orderId1, UUID orderId2) {
        var response = restTemplate.exchange(
                RequestEntity.get("/orders?sort=id").build(),
                new ParameterizedTypeReference<Collection<ListOrdersUseCase.OrderView>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .containsExactly(
                        new ListOrdersUseCase.OrderView(
                                orderId1,
                                100,
                                OrderStatus.CANCELED,
                                TestConfig.TIMESTAMP,
                                Optional.of(TestConfig.TIMESTAMP)
                        ),
                        new ListOrdersUseCase.OrderView(
                                orderId2,
                                100,
                                OrderStatus.COMPLETED,
                                TestConfig.TIMESTAMP,
                                Optional.of(TestConfig.TIMESTAMP)
                        )
                );
    }

    @Test
    @Order(1300)
    void deleteRepairer(UUID repairerId) {
        var response = restTemplate.exchange(
                RequestEntity.delete("/repairers/{id}", repairerId).build(),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @Order(1400)
    void deleteGarageSlot(UUID garageSlotId) {
        var response = restTemplate.exchange(
                RequestEntity.delete("/garage-slots/{id}", garageSlotId).build(),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @Order(1500)
    void listOrdersWithoutSortParameter() {
        var response = restTemplate.getForEntity("/orders", Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @TestConfiguration
    static class TestConfig {

        private static final Instant TIMESTAMP = Instant.parse("2000-01-01T00:00:00.00Z");

        @Bean
        @Primary
        Clock fixed() {
            return Clock.fixed(TIMESTAMP, ZoneOffset.UTC);
        }
    }

    private record Health(String status) {

        private Health() {
            this("UP");
        }
    }
}
