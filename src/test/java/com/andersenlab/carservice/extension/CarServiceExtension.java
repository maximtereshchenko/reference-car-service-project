package com.andersenlab.carservice.extension;

import com.andersenlab.carservice.application.InMemoryGarageSlotStore;
import com.andersenlab.carservice.application.InMemoryOrderStore;
import com.andersenlab.carservice.application.InMemoryRepairerStore;
import com.andersenlab.carservice.domain.CarServiceModule;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public final class CarServiceExtension implements ParameterResolver {

    private final Instant timestamp = Instant.parse("2000-01-01T00:00:00.00Z");

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        var type = parameterContext.getParameter().getType();
        return type == CarServiceModule.class || type == Instant.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        if (parameterContext.getParameter().getType() == Instant.class) {
            return timestamp;
        }
        return new CarServiceModule(
                new InMemoryRepairerStore(),
                new InMemoryGarageSlotStore(),
                new InMemoryOrderStore(),
                Clock.fixed(timestamp, ZoneOffset.UTC)
        );
    }
}
