package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.external.MessageBroker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

@AutoConfiguration
@ConditionalOnProperty(value = "car-service.artemis.enabled", havingValue = "true")
@EnableJms
class CarServiceArtemisAutoConfiguration {

    @Bean
    MessageBroker messageBroker(JmsTemplate jmsTemplate, @Value("${car-service.artemis.queue}") String queue) {
        return new ArtemisMessageBroker(jmsTemplate, queue);
    }
}
