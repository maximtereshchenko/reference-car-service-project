package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.external.MessageBroker;
import org.springframework.jms.core.JmsTemplate;

import java.util.UUID;

final class ArtemisMessageBroker implements MessageBroker {

    private final JmsTemplate jmsTemplate;
    private final String queue;

    ArtemisMessageBroker(JmsTemplate jmsTemplate, String queue) {
        this.jmsTemplate = jmsTemplate;
        this.queue = queue;
    }

    @Override
    public void publishNewOrderId(UUID orderId) {
        jmsTemplate.convertAndSend(queue, orderId);
    }
}
