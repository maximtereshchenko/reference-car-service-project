package com.andersenlab.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.UUID;

class Consumer {

    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(topics = "${apache-kafka-consumer.kafka.topic}")
    void listenNewOrderId(@Payload UUID orderId) {
        LOG.info("Received order id {}", orderId);
    }
}
