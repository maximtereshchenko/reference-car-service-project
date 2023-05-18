package com.andersenlab.extension;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.util.Map;

public final class KeycloakExtension extends ContainerExtension<GenericContainer<?>> {

    KeycloakExtension() {
        super(
                new GenericContainer<>(
                        DockerImageName.parse(
                                "keycloak/keycloak:21.1"
                        )
                )
                        .withEnv("KEYCLOAK_ADMIN", "admin")
                        .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
                        .withEnv("KC_HOSTNAME_STRICT", "false")
                        .withCommand("start-dev --import-realm")
                        .withCopyFileToContainer(
                                MountableFile.forClasspathResource("./realm.json"),
                                "/opt/keycloak/data/import/realm.json"
                        )
                        .withExposedPorts(8080),
                Map.of(
                        "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                        container ->
                                "http://%s:%s/realms/car-service".formatted(
                                        container.getHost(),
                                        container.getFirstMappedPort()
                                )
                )
        );
    }
}
