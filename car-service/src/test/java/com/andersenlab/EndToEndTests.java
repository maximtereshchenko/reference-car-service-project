package com.andersenlab;

import com.andersenlab.carservice.port.usecase.*;
import com.andersenlab.extension.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                EndToEndTests.TestConfig.class,
                TestSecurityConfiguration.class
        }
)
@ActiveProfiles("apache-kafka")
@TestMethodOrder(NaturalMethodOrderer.class)
@ExtendWith({
        PredictableUUIDExtension.class,
        PostgreSqlExtension.class,
        ApacheKafkaExtension.class,
        KeycloakExtension.class,
        SkipRemainingOnFailureExtension.class
})
final class EndToEndTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Value("${car-service.kafka.topic}")
    private String topic;

    @Test
    void livenessProbeEnabled() {
        var response = restTemplate.getForEntity("/actuator/health/liveness", Health.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new Health());
    }

    @Test
    void readinessProbeEnabled() {
        var response = restTemplate.getForEntity("/actuator/health/readiness", Health.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new Health());
    }

    @Test
    void createRepairer(UUID repairerId) {
        var response = restTemplate.exchange(
                RequestEntity.post("/repairers")
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.HUMAN_RESOURCES_SPECIALIST)
                        .body(
                                Map.of(
                                        "id", repairerId.toString(),
                                        "name", "John"
                                )
                        ),
                UUID.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(repairerId);
    }

    @Test
    void createRepairerWithSameId(UUID repairerId) {
        var response = restTemplate.exchange(
                RequestEntity.post("/repairers")
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.HUMAN_RESOURCES_SPECIALIST)
                        .body(
                                Map.of(
                                        "id", repairerId.toString(),
                                        "name", "John"
                                )
                        ),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void listRepairers(UUID repairerId) {
        var response = restTemplate.exchange(
                RequestEntity.get("/repairers?sort=id")
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.HUMAN_RESOURCES_SPECIALIST)
                        .build(),
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
    void createGarageSlot(UUID garageSlot) {
        var response = restTemplate.exchange(
                RequestEntity.post("/garage-slots")
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.OPERATIONAL_MANAGER)
                        .body(Map.of("id", garageSlot.toString())),
                UUID.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(garageSlot);
    }

    @Test
    void listGarageSlots(UUID garageSlot) {
        var response = restTemplate.exchange(
                RequestEntity.get("/garage-slots?sort=id")
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.OPERATIONAL_MANAGER)
                        .build(),
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
    void createOrder(UUID orderId1) {
        var response = restTemplate.exchange(
                RequestEntity.post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.SALES_SPECIALIST)
                        .body(
                                Map.of(
                                        "id", orderId1.toString(),
                                        "price", "100"
                                )
                        ),
                UUID.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(orderId1);
        assertThat(kafkaTemplate.receive(topic, 0, 0, Duration.ofSeconds(10)))
                .isNotNull()
                .extracting(ConsumerRecord::value)
                .isEqualTo(orderId1.toString());
    }

    @Test
    void cancelOrder(UUID orderId1) {
        var response = restTemplate.exchange(
                RequestEntity.post("/orders/{id}/cancel", orderId1)
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.SALES_SPECIALIST)
                        .build(),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void assignGarageSlot(UUID orderId2, UUID garageSlotId) {
        restTemplate.exchange(
                RequestEntity.post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.SALES_SPECIALIST)
                        .body(
                                Map.of(
                                        "id", orderId2.toString(),
                                        "price", "100"
                                )
                        ),
                Void.class
        );
        var response = restTemplate.exchange(
                RequestEntity.post("/orders/{id}/assign/garage-slot/{garageSlotId}", orderId2, garageSlotId)
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.OPERATIONAL_MANAGER)
                        .build(),
                Void.class
        );

        assertThat(kafkaTemplate.receive(topic, 0, 1, Duration.ofSeconds(10)))
                .isNotNull()
                .extracting(ConsumerRecord::value)
                .isEqualTo(orderId2.toString());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void assignRepairer(UUID orderId2, UUID repairerId) {
        var response = restTemplate.exchange(
                RequestEntity.post("/orders/{id}/assign/repairer/{repairerId}", orderId2, repairerId)
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.OPERATIONAL_MANAGER)
                        .build(),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void completeOrder(UUID orderId2) {
        var response = restTemplate.exchange(
                RequestEntity.post("/orders/{id}/complete", orderId2)
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.OPERATIONAL_MANAGER)
                        .build(),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void viewOrder(UUID orderId2, UUID garageSlotId, UUID repairerId) {
        var response = restTemplate.exchange(
                RequestEntity.get("/orders/{id}", orderId2)
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.SALES_SPECIALIST)
                        .build(),
                ViewOrderUseCase.OrderView.class
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
    void listOrders(UUID orderId1, UUID orderId2) {
        var response = restTemplate.exchange(
                RequestEntity.get("/orders?sort=id")
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.SALES_SPECIALIST)
                        .build(),
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
    void deleteRepairer(UUID repairerId) {
        var response = restTemplate.exchange(
                RequestEntity.delete("/repairers/{id}", repairerId)
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.HUMAN_RESOURCES_SPECIALIST)
                        .build(),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteGarageSlot(UUID garageSlotId) {
        var response = restTemplate.exchange(
                RequestEntity.delete("/garage-slots/{id}", garageSlotId)
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.OPERATIONAL_MANAGER)
                        .build(),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void listOrdersWithoutSortParameter() {
        var response = restTemplate.exchange(
                RequestEntity.get("/orders")
                        .header(HttpHeaders.AUTHORIZATION, TestSecurityConfiguration.SALES_SPECIALIST)
                        .build(),
                Void.class
        );

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

        @Bean
        KafkaTemplate<String, String> kafkaTemplate(
                ProducerFactory<String, String> producerFactory,
                @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers
        ) {
            var kafkaTemplate = new KafkaTemplate<>(producerFactory);
            kafkaTemplate.setConsumerFactory(
                    new DefaultKafkaConsumerFactory<>(
                            Map.of(
                                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                                    ConsumerConfig.GROUP_ID_CONFIG, "test",
                                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class.getName(),
                                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()
                            )
                    )
            );
            return kafkaTemplate;
        }
    }

    private record Health(String status) {

        private Health() {
            this("UP");
        }
    }
}
