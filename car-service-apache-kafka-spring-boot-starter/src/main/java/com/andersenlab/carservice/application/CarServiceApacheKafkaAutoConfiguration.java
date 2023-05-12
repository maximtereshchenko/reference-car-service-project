package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.external.MessageBroker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@AutoConfiguration
class CarServiceApacheKafkaAutoConfiguration {

    @Bean
    MessageBroker messageBroker(KafkaProperties kafkaProperties, @Value("${car-service.kafka.topic}") String topic) {
        return new ApacheKafkaMessageBroker(
                new KafkaTemplate<>(
                        new DefaultKafkaProducerFactory<>(
                                kafkaProperties.buildProducerProperties()
                        )
                ),
                topic
        );
    }
}
