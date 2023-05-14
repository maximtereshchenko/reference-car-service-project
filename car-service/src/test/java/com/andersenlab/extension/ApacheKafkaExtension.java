package com.andersenlab.extension;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

public final class ApacheKafkaExtension extends ContainerExtension<KafkaContainer> {

    ApacheKafkaExtension() {
        super(
                new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0")),
                Map.of(
                        "car-service.kafka.topic", container -> "test",
                        "spring.kafka.bootstrap-servers", KafkaContainer::getBootstrapServers
                )
        );
    }
}
