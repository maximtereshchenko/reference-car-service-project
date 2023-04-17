package com.andersenlab.extension;

import com.andersenlab.Main;
import com.andersenlab.StaticSettings;
import com.andersenlab.carservice.application.HttpInterface;
import org.junit.jupiter.api.extension.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public final class JettyExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private final Instant timestamp = Instant.parse("2000-01-01T00:00:00.00Z");
    private Path temporaryDirectory;
    private HttpInterface httpInterface;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        temporaryDirectory = Files.createTempDirectory(null);
        httpInterface = Main.httpInterface(
                new StaticSettings(temporaryDirectory.resolve("state.json")),
                Clock.fixed(timestamp, ZoneOffset.UTC)
        );
        httpInterface.run();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        httpInterface.stop();
        temporaryDirectory.toFile().deleteOnExit();
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
