package com.andersenlab.carservice;

import com.andersenlab.carservice.application.InMemoryGarageSlotStore;
import com.andersenlab.carservice.application.InMemoryRepairerStore;
import com.andersenlab.carservice.domain.CarServiceModule;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public final class CarServiceExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return CarServiceModule.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return new CarServiceModule(new InMemoryRepairerStore(), new InMemoryGarageSlotStore());
    }
}
