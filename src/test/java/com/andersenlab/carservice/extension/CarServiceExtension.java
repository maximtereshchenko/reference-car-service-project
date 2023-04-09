package com.andersenlab.carservice.extension;

import com.andersenlab.carservice.application.InMemoryGarageSlotStore;
import com.andersenlab.carservice.application.InMemoryOrderStore;
import com.andersenlab.carservice.application.InMemoryRepairerStore;
import com.andersenlab.carservice.domain.CarServiceModule;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CarServiceExtension implements ParameterResolver {

    private final Map<String, ManualClock> clocks = new ConcurrentHashMap<>();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        var type = parameterContext.getParameter().getType();
        return type == CarServiceModule.class || type == ManualClock.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        var clock = clocks.computeIfAbsent(extensionContext.getUniqueId(), id -> new ManualClock());
        if (parameterContext.getParameter().getType() == ManualClock.class) {
            return clock;
        }
        return new CarServiceModule(
                new InMemoryRepairerStore(),
                new InMemoryGarageSlotStore(),
                new InMemoryOrderStore(),
                clock
        );
    }
}