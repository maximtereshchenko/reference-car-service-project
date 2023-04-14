package com.andersenlab;

import com.andersenlab.carservice.application.HttpInterface;
import com.andersenlab.carservice.application.storage.OnDiskGarageSlotStore;
import com.andersenlab.carservice.application.storage.OnDiskOrderStore;
import com.andersenlab.carservice.application.storage.OnDiskRepairerStore;
import com.andersenlab.carservice.application.storage.StateFile;
import com.andersenlab.carservice.domain.Module;
import com.andersenlab.carservice.port.usecase.*;
import com.andersenlab.extension.PredictableUUIDExtension;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(PredictableUUIDExtension.class)
final class EndToEndTests {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final Instant timestamp = Instant.parse("2000-01-01T00:00:00.00Z");
    private HttpInterface httpInterface;

    @BeforeAll
    void setUp(@TempDir Path temporary) {
        var stateFile = new StateFile(temporary.resolve("state.json"));
        httpInterface = HttpInterface.forModule(
                new Module.Builder()
                        .withRepairerStore(new OnDiskRepairerStore(stateFile))
                        .withGarageSlotStore(new OnDiskGarageSlotStore(stateFile))
                        .withOrderStore(new OnDiskOrderStore(stateFile))
                        .withClock(Clock.fixed(timestamp, ZoneOffset.UTC))
                        .build()
        );
        httpInterface.run();
    }

    @AfterAll
    void cleanUp() {
        httpInterface.stop();
    }

    @Test
    @Order(0)
    void createRepairer(UUID repairerId) throws Exception {
        var response = post(
                "/repairers",
                Map.of(
                        "id", repairerId.toString(),
                        "name", "John"
                ),
                new TypeReference<UUID>() {}
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo(repairerId);
    }

    @Test
    @Order(1)
    void createRepairerWithSameId(UUID repairerId) throws Exception {
        var response = post(
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
    void listRepairers(UUID repairerId) throws Exception {
        var response = get(
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
    void createGarageSlot(UUID garageSlot) throws Exception {
        var response = post(
                "/garage-slots",
                Map.of("id", garageSlot.toString()),
                new TypeReference<UUID>() {}
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo(garageSlot);
    }

    @Test
    @Order(4)
    void listGarageSlots(UUID garageSlot) throws Exception {
        var response = get(
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
    void createOrder(UUID orderId1) throws Exception {
        var response = post(
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
    void cancelOrder(UUID orderId1) throws Exception {
        var response = post("/orders/cancel", Map.of("id", orderId1.toString()));

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(7)
    void assignGarageSlot(UUID orderId2, UUID garageSlot) throws Exception {
        post(
                "/orders",
                Map.of(
                        "id", orderId2.toString(),
                        "price", "100"
                )
        );
        var response = post(
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
    void assignRepairer(UUID orderId2, UUID repairerId) throws Exception {
        var response = post(
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
    void completeOrder(UUID orderId2) throws Exception {
        var response = post(
                "/orders/complete",
                Map.of("id", orderId2.toString())
        );

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(10)
    void viewOrder(UUID orderId2, UUID garageSlotId, UUID repairerId) throws Exception {
        var response = get(
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
    void listOrders(UUID orderId1, UUID orderId2) throws Exception {
        var response = get(
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
    void deleteRepairer(UUID repairerId) throws Exception {
        var response = delete("/repairers/" + repairerId);

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    @Order(13)
    void deleteGarageSlot(UUID garageSlotId) throws Exception {
        var response = delete("/garage-slots/" + garageSlotId);

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    @Order(14)
    void listOrdersWithoutSortParameter() throws Exception {
        var response = HttpClient.newHttpClient()
                .send(
                        request("/orders")
                                .GET()
                                .build(),
                        HttpResponse.BodyHandlers.discarding()
                );

        assertThat(response.statusCode()).isEqualTo(400);
    }

    private HttpResponse<Void> delete(String uri) throws Exception {
        return HttpClient.newHttpClient()
                .send(
                        request(uri)
                                .DELETE()
                                .build(),
                        HttpResponse.BodyHandlers.discarding()
                );
    }

    private <T> HttpResponse<T> post(String uri, Map<String, String> body, TypeReference<T> typeReference)
            throws Exception {
        return HttpClient.newHttpClient()
                .send(
                        request(uri)
                                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                                .build(),
                        new JsonBodyHandler<>(objectMapper, typeReference)
                );
    }

    private HttpResponse<Void> post(String uri, Map<String, String> body) throws Exception {
        return HttpClient.newHttpClient()
                .send(
                        request(uri)
                                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                                .build(),
                        HttpResponse.BodyHandlers.discarding()
                );
    }

    private <T> HttpResponse<T> get(String uri, TypeReference<T> typeReference) throws Exception {
        return HttpClient.newHttpClient()
                .send(
                        request(uri)
                                .GET()
                                .build(),
                        new JsonBodyHandler<>(objectMapper, typeReference)
                );
    }

    private HttpRequest.Builder request(String uri) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + uri))
                .header("Content-Type", "application/json");
    }

    private static final class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {

        private final ObjectMapper objectMapper;
        private final TypeReference<T> typeReference;
        private final HttpResponse.BodyHandler<String> original = HttpResponse.BodyHandlers.ofString();

        private JsonBodyHandler(ObjectMapper objectMapper, TypeReference<T> typeReference) {
            this.objectMapper = objectMapper;
            this.typeReference = typeReference;
        }

        @Override
        public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
            return new JsonBodySubscriber<>(objectMapper, typeReference, original.apply(responseInfo));
        }

        private static final class JsonBodySubscriber<T> implements HttpResponse.BodySubscriber<T> {

            private final ObjectMapper objectMapper;
            private final TypeReference<T> typeReference;
            private final HttpResponse.BodySubscriber<String> original;

            private JsonBodySubscriber(
                    ObjectMapper objectMapper,
                    TypeReference<T> typeReference,
                    HttpResponse.BodySubscriber<String> original
            ) {
                this.objectMapper = objectMapper;
                this.typeReference = typeReference;
                this.original = original;
            }

            @Override
            public CompletionStage<T> getBody() {
                return original.getBody()
                        .thenApply(this::json);
            }

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                original.onSubscribe(subscription);
            }

            @Override
            public void onNext(List<ByteBuffer> item) {
                original.onNext(item);
            }

            @Override
            public void onError(Throwable throwable) {
                original.onError(throwable);
            }

            @Override
            public void onComplete() {
                original.onComplete();
            }

            private T json(String raw) {
                try {
                    return objectMapper.readValue(raw, typeReference);
                } catch (JsonProcessingException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }
}
