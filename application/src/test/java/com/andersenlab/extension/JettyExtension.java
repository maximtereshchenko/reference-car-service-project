package com.andersenlab.extension;

import com.andersenlab.Application;
import com.andersenlab.StaticSettings;
import org.junit.jupiter.api.extension.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public final class JettyExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private final Instant timestamp = Instant.parse("2000-01-01T00:00:00.00Z");
    private Application application;

    @Override
    public void beforeAll(ExtensionContext context) {
        application = Application.from(new StaticSettings(), Clock.fixed(timestamp, ZoneOffset.UTC));
        application.run();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        application.stop();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        var type = parameterContext.getParameter().getType();
        return type == Instant.class || type == JsonHttpClient.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (parameterContext.getParameter().getType() == Instant.class) {
            return timestamp;
        }
        return new JsonHttpClient();
    }
}
