package com.andersenlab.extension;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

public final class PostgreSqlExtension extends ContainerExtension<PostgreSQLContainer<?>> {

    PostgreSqlExtension() {
        super(
                new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.2-alpine")),
                Map.of(
                        "spring.datasource.url", JdbcDatabaseContainer::getJdbcUrl,
                        "spring.datasource.username", JdbcDatabaseContainer::getUsername,
                        "spring.datasource.password", JdbcDatabaseContainer::getPassword
                )
        );
    }
}
