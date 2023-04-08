package com.andersenlab.carservice.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.UUID;

public final class PredictableUUIDExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return UUID.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        var name = parameterContext.getParameter().getName();
        return UUID.fromString("00000000-0000-0000-0000-00000000000%c".formatted(name.charAt(name.length() - 1)));
    }
}
