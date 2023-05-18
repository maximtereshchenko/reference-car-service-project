package com.andersenlab;

import com.andersenlab.carservice.port.usecase.ListOrdersUseCase;
import com.andersenlab.carservice.port.usecase.OrderStatus;
import com.andersenlab.extension.ApacheKafkaExtension;
import com.andersenlab.extension.KeycloakExtension;
import com.andersenlab.extension.PostgreSqlExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestSecurityConfiguration.class)
@ActiveProfiles("apache-kafka")
@ExtendWith({PostgreSqlExtension.class, ApacheKafkaExtension.class, KeycloakExtension.class})
final class LoadTests {

    private final CountDownLatch latch = new CountDownLatch(1);
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void givenManyConcurrentUpdates_whenRead_thenExpectedTotalNumberOfRecordsReturned() throws Exception {
        var executorService = Executors.newWorkStealingPool();
        var futures = IntStream.range(0, 1000)
                .mapToObj(i -> executorService.submit(() -> completeOrderSafe(restTemplate)))
                .toList();

        latch.countDown();
        executorService.shutdown();

        for (var future : futures) {
            future.get().onComplete();
        }
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        var response = restTemplate.exchange(
                RequestEntity.get("/orders?sort=id").build(),
                new ParameterizedTypeReference<Collection<ListOrdersUseCase.OrderView>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .hasSize(1000)
                .allMatch(orderView -> orderView.status() == OrderStatus.COMPLETED);
    }

    private Result completeOrderSafe(TestRestTemplate restTemplate) {
        try {
            return completeOrder(restTemplate).orElse(new Success());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Failure(e);
        } catch (Exception e) {
            return new Failure(e);
        }
    }

    private Optional<Result> completeOrder(TestRestTemplate restTemplate) throws InterruptedException {
        latch.await();

        var garageSlotId = UUID.randomUUID();
        var repairerId = UUID.randomUUID();
        var orderId = UUID.randomUUID();

        return post(
                restTemplate,
                "/garage-slots",
                Map.of("id", garageSlotId.toString())
        )
                .or(() ->
                        post(
                                restTemplate,
                                "/repairers",
                                Map.of(
                                        "id", repairerId.toString(),
                                        "name", "John"
                                )
                        )
                )
                .or(() ->
                        post(
                                restTemplate,
                                "/orders",
                                Map.of(
                                        "id", orderId.toString(),
                                        "price", "100"
                                )
                        )
                )
                .or(() ->
                        post(
                                restTemplate,
                                "/orders/{id}/assign/garage-slot/{garageSlotId}",
                                null,
                                orderId,
                                garageSlotId
                        )
                )
                .or(() ->
                        post(
                                restTemplate,
                                "/orders/{id}/assign/repairer/{repairerId}",
                                null,
                                orderId,
                                repairerId
                        )
                )
                .or(() ->
                        post(
                                restTemplate,
                                "/orders/{id}/complete",
                                null,
                                orderId
                        )
                );
    }

    private Optional<Result> post(
            TestRestTemplate restTemplate,
            String uri,
            Map<String, String> body,
            UUID... uriVars
    ) {
        var response = restTemplate.postForEntity(
                uri,
                body,
                Void.class,
                (Object[]) Arrays.copyOf(uriVars, uriVars.length)
        );
        if (response.getStatusCode() != HttpStatus.OK) {
            return Optional.of(new IncorrectResponse(response));
        }
        return Optional.empty();
    }

    private sealed interface Result permits Failure, IncorrectResponse, Success {

        void onComplete();
    }

    private record Failure(Exception e) implements Result {

        @Override
        public void onComplete() {
            fail("Caught exception", e);
        }
    }

    private record IncorrectResponse(ResponseEntity<Void> response) implements Result {

        @Override
        public void onComplete() {
            fail("HTTP request failed with response " + response);
        }
    }

    private static final class Success implements Result {

        @Override
        public void onComplete() {
            //empty
        }
    }
}
