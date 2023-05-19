package com.andersenlab.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

class Consumer {

    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(topics = "${apache-kafka-consumer.kafka.topic}")
    void listenNewOrderId(String orderId) {
        LOG.info("Received order id {}", orderId);
    }
}
