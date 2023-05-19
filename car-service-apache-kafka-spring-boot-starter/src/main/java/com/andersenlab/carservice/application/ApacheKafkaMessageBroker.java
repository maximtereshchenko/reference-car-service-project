package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.external.MessageBroker;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

final class ApacheKafkaMessageBroker implements MessageBroker {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    ApacheKafkaMessageBroker(KafkaTemplate<String, String> kafkaTemplate, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publishNewOrderId(UUID orderId) {
        try {
            kafkaTemplate.send(topic, orderId.toString()).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CouldNotPublish(e);
        } catch (ExecutionException e) {
            throw new CouldNotPublish(e);
        }
    }

    private static final class CouldNotPublish extends RuntimeException {

        CouldNotPublish(Throwable cause) {
            super(cause);
        }
    }
}
