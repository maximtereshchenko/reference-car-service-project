package com.andersenlab;

import com.andersenlab.carservice.application.HttpInterface;
import com.andersenlab.carservice.application.storage.jdbc.JdbcOrderStore;
import com.andersenlab.carservice.application.storage.jdbc.TransactionalCarServiceModule;
import com.andersenlab.carservice.application.storage.jpa.Database;
import com.andersenlab.carservice.application.storage.jpa.JpaGarageSlotStore;
import com.andersenlab.carservice.application.storage.jpa.JpaRepairerStore;
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
        var jdbcUrl = settings.jdbcUrl();
        var database = database(jdbcUrl);
        var database1 = new Database(jdbcUrl);
        var module = new Module.Builder()
                .withRepairerStore(new JpaRepairerStore(database1))
                .withGarageSlotStore(new JpaGarageSlotStore(database1))
                .withOrderStore(new JdbcOrderStore(database))
                .withClock(Clock.systemDefaultZone())
                .garageSlotAdditionEnabled(settings.isGarageSlotAdditionEnabled())
                .garageSlotDeletionEnabled(settings.isGarageSlotDeletionEnabled())
                .withClock(clock)
                .build();
        return new com.andersenlab.carservice.application.storage.jpa.TransactionalCarServiceModule(new TransactionalCarServiceModule(module, database), database1);
    }

    private static com.andersenlab.carservice.application.storage.jdbc.Database database(String jdbcUrl) {
        var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);

        migrate(dataSource);

        return new com.andersenlab.carservice.application.storage.jdbc.Database(dataSource);
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
