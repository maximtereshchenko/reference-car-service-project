package com.andersenlab.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.UUID;

public final class PredictableUUIDExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == UUID.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return UUID.fromString(
                "00000000-0000-0000-0000-00000000000" +
                        digit(lastCharacter(parameterContext.getParameter().getName()))
        );
    }

    private char lastCharacter(CharSequence charSequence) {
        return charSequence.charAt(charSequence.length() - 1);
    }

    private int digit(char lastCharacter) {
        if (Character.isDigit(lastCharacter)) {
            return Character.digit(lastCharacter, 10);
        }
        return 0;
    }
}
