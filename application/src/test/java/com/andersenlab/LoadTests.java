package com.andersenlab;

import com.andersenlab.carservice.port.usecase.ListOrdersUseCase;
import com.andersenlab.extension.JettyExtension;
import com.andersenlab.extension.JsonHttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(JettyExtension.class)
final class LoadTests {

    private final CountDownLatch latch = new CountDownLatch(1);

    @Test
    void givenManyConcurrentUpdates_whenRead_thenExpectedTotalNumberOfRecordsReturned(JsonHttpClient client)
            throws Exception {
        var executorService = Executors.newWorkStealingPool();
        var futures = IntStream.range(0, 1000)
                .mapToObj(i -> executorService.submit(() -> completeOrder(client)))
                .toList();

        latch.countDown();
        executorService.shutdown();

        for (var future : futures) {
            future.get().onComplete();
        }
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        var response = client.get(
                "/orders?sort=id",
                new TypeReference<Collection<ListOrdersUseCase.OrderView>>() {}
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).hasSize(1000);
    }

    private Result completeOrder(JsonHttpClient client) {
        try {
            latch.await();

            var garageSlotId = UUID.randomUUID();
            var repairerId = UUID.randomUUID();
            var orderId = UUID.randomUUID();
            return post(
                    client,
                    "/garage-slots",
                    Map.of("id", garageSlotId.toString())
            )
                    .or(() ->
                            post(
                                    client,
                                    "/repairers",
                                    Map.of(
                                            "id", repairerId.toString(),
                                            "name", "John"
                                    )
                            )
                    )
                    .or(() ->
                            post(
                                    client,
                                    "/orders",
                                    Map.of(
                                            "id", orderId.toString(),
                                            "price", "100"
                                    )
                            )
                    )
                    .or(() ->
                            post(
                                    client,
                                    "/orders/assign/garage-slot",
                                    Map.of(
                                            "id", orderId.toString(),
                                            "garageSlotId", garageSlotId.toString()
                                    )
                            )
                    )
                    .or(() ->
                            post(
                                    client,
                                    "/orders/assign/repairer",
                                    Map.of(
                                            "id", orderId.toString(),
                                            "repairerId", repairerId.toString()
                                    )
                            )
                    )
                    .or(() ->
                            post(
                                    client,
                                    "/orders/complete",
                                    Map.of("id", orderId.toString())
                            )
                    )
                    .orElse(new Success());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Failure(e);
        } catch (Exception e) {
            return new Failure(e);
        }
    }

    private Optional<Result> post(JsonHttpClient client, String uri, Map<String, String> body) {
        var response = client.post(uri, body);
        if (response.statusCode() != 200) {
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

    private record IncorrectResponse(HttpResponse<Void> response) implements Result {

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
