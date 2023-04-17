package com.andersenlab;

import com.andersenlab.application.storage.ThreadSafeGarageSlotStore;
import com.andersenlab.application.storage.ThreadSafeOrderStore;
import com.andersenlab.application.storage.ThreadSafeRepairerStore;
import com.andersenlab.carservice.application.HttpInterface;
import com.andersenlab.carservice.application.storage.OnDiskGarageSlotStore;
import com.andersenlab.carservice.application.storage.OnDiskOrderStore;
import com.andersenlab.carservice.application.storage.OnDiskRepairerStore;
import com.andersenlab.carservice.application.storage.StateFile;
import com.andersenlab.carservice.domain.CarServiceModule;
import com.andersenlab.carservice.domain.Module;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.Objects;

public final class Main {

    public static void main(String[] args) throws URISyntaxException {
        httpInterface(settings(), Clock.systemDefaultZone()).run();
    }

    public static HttpInterface httpInterface(Settings settings, Clock clock) {
        return HttpInterface.forModule(module(settings, clock));
    }

    private static CarServiceModule module(Settings settings, Clock clock) {
        var stateFile = new StateFile(settings.stateFilePath());
        return new Module.Builder()
                .withRepairerStore(new ThreadSafeRepairerStore(new OnDiskRepairerStore(stateFile)))
                .withGarageSlotStore(new ThreadSafeGarageSlotStore(new OnDiskGarageSlotStore(stateFile)))
                .withOrderStore(new ThreadSafeOrderStore(new OnDiskOrderStore(stateFile)))
                .withClock(Clock.systemDefaultZone())
                .garageSlotAdditionEnabled(settings.isGarageSlotAdditionEnabled())
                .garageSlotDeletionEnabled(settings.isGarageSlotDeletionEnabled())
                .withClock(clock)
                .build();
    }

    private static Settings settings() throws URISyntaxException {
        return TomlSettings.from(
                Paths.get(
                        Objects.requireNonNull(
                                        Thread.currentThread()
                                                .getContextClassLoader()
                                                .getResource("application.toml")
                                )
                                .toURI()
                )
        );
    }
}
