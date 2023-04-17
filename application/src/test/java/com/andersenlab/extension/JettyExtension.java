package com.andersenlab.extension;

import com.andersenlab.carservice.application.HttpInterface;
import com.andersenlab.carservice.application.storage.OnDiskGarageSlotStore;
import com.andersenlab.carservice.application.storage.OnDiskOrderStore;
import com.andersenlab.carservice.application.storage.OnDiskRepairerStore;
import com.andersenlab.carservice.application.storage.StateFile;
import com.andersenlab.carservice.domain.Module;
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
        var stateFile = new StateFile(temporaryDirectory.resolve("state.json"));
        httpInterface = HttpInterface.forModule(
                new Module.Builder()
                        .withRepairerStore(new OnDiskRepairerStore(stateFile))
                        .withGarageSlotStore(new OnDiskGarageSlotStore(stateFile))
                        .withOrderStore(new OnDiskOrderStore(stateFile))
                        .withClock(Clock.fixed(timestamp, ZoneOffset.UTC))
                        .build()
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
