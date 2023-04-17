package com.andersenlab;

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

final class Main {

    public static void main(String[] args) throws URISyntaxException {
        HttpInterface.forModule(module()).run();
    }

    private static CarServiceModule module() throws URISyntaxException {
        var settings = settings();
        var stateFile = new StateFile(settings.stateFilePath());
        return new Module.Builder()
                .withRepairerStore(new ThreadSafeRepairerStore(new OnDiskRepairerStore(stateFile)))
                .withGarageSlotStore(new OnDiskGarageSlotStore(stateFile))
                .withOrderStore(new OnDiskOrderStore(stateFile))
                .withClock(Clock.systemDefaultZone())
                .garageSlotAdditionEnabled(settings.isGarageSlotAdditionEnabled())
                .garageSlotDeletionEnabled(settings.isGarageSlotDeletionEnabled())
                .build();
    }

    private static Settings settings() throws URISyntaxException {
        return Settings.from(
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
