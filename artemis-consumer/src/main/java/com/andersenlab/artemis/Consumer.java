package com.andersenlab.artemis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

import java.util.UUID;

class Consumer {

    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    @JmsListener(destination = "${artemis-consumer.artemis.queue}")
    void listenNewOrderId(UUID orderId) {
        LOG.info("Received order id {}", orderId);
    }
}
