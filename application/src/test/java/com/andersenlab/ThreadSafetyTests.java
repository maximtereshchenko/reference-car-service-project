package com.andersenlab;

import com.andersenlab.carservice.port.usecase.ListGarageSlotsUseCase;
import com.andersenlab.extension.JettyExtension;
import com.andersenlab.extension.JsonHttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(JettyExtension.class)
final class ThreadSafetyTests {

    private final CountDownLatch latch = new CountDownLatch(1);

    @Test
    void givenManyConcurrentUpdated_whenRead_thenExpectedTotalNumberOfRecordsReturned(JsonHttpClient client)
            throws Exception {
        var executorService = Executors.newWorkStealingPool();
        var futures = IntStream.range(0, 1000)
                .mapToObj(i -> executorService.submit(() -> addGarageSlot(client)))
                .toList();

        latch.countDown();
        executorService.shutdown();

        for (var future : futures) {
            future.get().onComplete();
        }
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        var response = client.get(
                "/garage-slots?sort=id",
                new TypeReference<Collection<ListGarageSlotsUseCase.GarageSlotView>>() {}
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).hasSize(1000);
    }

    private Result addGarageSlot(JsonHttpClient client) {
        try {
            latch.await();

            var response = client.post(
                    "/garage-slots",
                    Map.of("id", UUID.randomUUID().toString())
            );

            if (response.statusCode() != 200) {
                return new CouldNotAddGarageSlot(response.statusCode());
            }
            return new Success();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Failure(e);
        } catch (Exception e) {
            return new Failure(e);
        }
    }

    private sealed interface Result permits Failure, CouldNotAddGarageSlot, Success {

        void onComplete();
    }

    private record Failure(Exception e) implements Result {

        @Override
        public void onComplete() {
            fail("Caught exception", e);
        }
    }

    private record CouldNotAddGarageSlot(int statusCode) implements Result {

        @Override
        public void onComplete() {
            fail("Could not add garage slot with status code " + statusCode);
        }
    }

    private static final class Success implements Result {

        @Override
        public void onComplete() {
            //empty
        }
    }
}
