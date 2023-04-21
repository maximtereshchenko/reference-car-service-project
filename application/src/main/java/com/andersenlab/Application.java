package com.andersenlab;

import com.andersenlab.carservice.application.HttpInterface;
import com.andersenlab.carservice.application.storage.jpa.*;
import com.andersenlab.carservice.domain.CarServiceModule;
import com.andersenlab.carservice.domain.Module;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

import java.time.Clock;

public final class Application {

    private final HttpInterface httpInterface;

    private Application(HttpInterface httpInterface) {
        this.httpInterface = httpInterface;
    }

    public static Application from(Settings settings, Clock clock) {
        migrate(settings.jdbcUrl());
        return new Application(HttpInterface.forModule(module(settings, clock)));
    }

    private static CarServiceModule module(Settings settings, Clock clock) {
        var database = new Database(settings.jdbcUrl());
        var module = new Module.Builder()
                .withRepairerStore(new JpaRepairerStore(database))
                .withGarageSlotStore(new JpaGarageSlotStore(database))
                .withOrderStore(new JpaOrderStore(database))
                .withClock(Clock.systemDefaultZone())
                .garageSlotAdditionEnabled(settings.isGarageSlotAdditionEnabled())
                .garageSlotDeletionEnabled(settings.isGarageSlotDeletionEnabled())
                .withClock(clock)
                .build();
        return new TransactionalCarServiceModule(module, database);
    }

    private static void migrate(String jdbcUrl) {
        var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:migrations")
                .loggers("slf4j")
                .load()
                .migrate();
    }

    public void run() {
        httpInterface.run();
    }

    public void stop() {
        httpInterface.stop();
    }
}
