package com.andersenlab.extension;

import com.andersenlab.carservice.application.storage.inmemory.InMemoryGarageSlotStore;
import com.andersenlab.carservice.application.storage.inmemory.InMemoryOrderStore;
import com.andersenlab.carservice.application.storage.inmemory.InMemoryRepairerStore;
import com.andersenlab.carservice.domain.CarServiceModule;
import com.andersenlab.carservice.domain.Module;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Map;

public final class CarServiceExtension implements ParameterResolver {

    private final ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(CarServiceExtension.class);

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return extensionContext.getStore(namespace)
                .getOrComputeIfAbsent(extensionContext.getUniqueId(), id -> Context.create(), Context.class)
                .supports(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return extensionContext.getStore(namespace)
                .get(extensionContext.getUniqueId(), Context.class)
                .parameter(parameterContext.getParameter().getType());
    }

    private record Context(Map<Class<?>, Object> parameters) {

        private Context(ManualClock clock, FakeMessageBroker messageBroker, Module.Builder builder) {
            this(
                    Map.of(
                            ManualClock.class, clock,
                            FakeMessageBroker.class, messageBroker,
                            Module.Builder.class, builder,
                            CarServiceModule.class, builder.build()
                    )
            );
        }

        static Context create() {
            var clock = new ManualClock();
            var messageBroker = new FakeMessageBroker();
            var builder = new Module.Builder()
                    .withRepairerStore(new InMemoryRepairerStore())
                    .withGarageSlotStore(new InMemoryGarageSlotStore())
                    .withOrderStore(new InMemoryOrderStore())
                    .withClock(clock)
                    .withMessageBroker(messageBroker);
            return new Context(clock, messageBroker, builder);
        }

        boolean supports(Class<?> type) {
            return parameters.containsKey(type);
        }

        Object parameter(Class<?> type) {
            return parameters.get(type);
        }
    }
}
