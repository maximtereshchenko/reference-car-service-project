package com.andersenlab.extension;

import com.andersenlab.carservice.application.storage.InMemoryGarageSlotStore;
import com.andersenlab.carservice.application.storage.InMemoryOrderStore;
import com.andersenlab.carservice.application.storage.InMemoryRepairerStore;
import com.andersenlab.carservice.domain.CarServiceModule;
import com.andersenlab.carservice.domain.Module;
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
        return type == CarServiceModule.class ||
                type == ManualClock.class ||
                type == Module.Builder.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        var type = parameterContext.getParameter().getType();
        var clock = clocks.computeIfAbsent(extensionContext.getUniqueId(), id -> new ManualClock());
        if (type == ManualClock.class) {
            return clock;
        }
        var builder = new Module.Builder()
                .withRepairerStore(new InMemoryRepairerStore())
                .withGarageSlotStore(new InMemoryGarageSlotStore())
                .withOrderStore(new InMemoryOrderStore())
                .withClock(clock);
        if (type == Module.Builder.class) {
            return builder;
        }
        return builder.build();
    }
}
