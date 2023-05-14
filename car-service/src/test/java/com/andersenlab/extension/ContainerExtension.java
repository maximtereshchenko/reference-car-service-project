package com.andersenlab.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

import java.util.Map;
import java.util.function.Function;

abstract class ContainerExtension<T extends GenericContainer<?>> implements BeforeAllCallback, AfterAllCallback {

    private final T container;
    private final Map<String, Function<T, String>> propertyAccessors;

    ContainerExtension(T container, Map<String, Function<T, String>> propertyAccessors) {
        this.container = container;
        this.propertyAccessors = Map.copyOf(propertyAccessors);
    }

    @Override
    public final void beforeAll(ExtensionContext context) {
        container.start();
        propertyAccessors.forEach((key, accessor) -> System.setProperty(key, accessor.apply(container)));
    }

    @Override
    public final void afterAll(ExtensionContext context) {
        container.stop();
        propertyAccessors.keySet().forEach(System::clearProperty);
    }
}
