package com.andersenlab.extension;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

public final class ArtemisExtension extends ContainerExtension<GenericContainer<?>> {

    ArtemisExtension() {
        super(
                new GenericContainer<>(
                        DockerImageName.parse(
                                "quay.io/artemiscloud/activemq-artemis-broker:1.0.17"
                        )
                )
                        .withEnv("AMQ_USER", "artemis")
                        .withEnv("AMQ_PASSWORD", "artemis")
                        .withExposedPorts(61616),
                Map.of(
                        "car-service.artemis.queue", container -> "test",
                        "spring.artemis.brokerUrl", container -> "tcp://localhost:" + container.getFirstMappedPort(),
                        "spring.artemis.user", container -> "artemis",
                        "spring.artemis.password", container -> "artemis"
                )
        );
    }
}
