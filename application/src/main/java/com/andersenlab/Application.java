package com.andersenlab;

import com.andersenlab.carservice.application.HttpInterface;
import com.andersenlab.carservice.application.storage.*;
import com.andersenlab.carservice.domain.CarServiceModule;
import com.andersenlab.carservice.domain.Module;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.time.Clock;

public final class Application {

    private final HttpInterface httpInterface;

    private Application(HttpInterface httpInterface) {
        this.httpInterface = httpInterface;
    }

    public static Application from(Settings settings, Clock clock) {
        return new Application(HttpInterface.forModule(module(settings, clock)));
    }

    private static CarServiceModule module(Settings settings, Clock clock) {
        var database = database(settings.jdbcUrl());
        var module = new Module.Builder()
                .withRepairerStore(new JdbcRepairerStore(database))
                .withGarageSlotStore(new JdbcGarageSlotStore(database))
                .withOrderStore(new JdbcOrderStore(database))
                .withClock(Clock.systemDefaultZone())
                .garageSlotAdditionEnabled(settings.isGarageSlotAdditionEnabled())
                .garageSlotDeletionEnabled(settings.isGarageSlotDeletionEnabled())
                .withClock(clock)
                .build();
        return new TransactionalCarServiceModule(module, database);
    }

    private static Database database(String jdbcUrl) {
        var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);

        migrate(dataSource);

        return new Database(dataSource);
    }

    private static void migrate(DataSource dataSource) {
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
