package com.andersenlab;

import com.andersenlab.carservice.application.storage.jpa.*;
import com.andersenlab.carservice.domain.CarServiceModule;
import com.andersenlab.carservice.domain.Module;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootConfiguration
@EnableAutoConfiguration
class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }

    @Bean
    CarServiceModule module(
            @Value("${application.jdbc.url}") String jdbcUrl,
            @Value("${application.garageSlots.addition.enabled}") boolean isGarageSlotAdditionEnabled,
            @Value("${application.garageSlots.deletion.enabled}") boolean isGarageSlotDeletionEnabled,
            Clock clock
    ) {
        migrate(jdbcUrl);
        var database = new Database(jdbcUrl);
        var module = new Module.Builder()
                .withRepairerStore(new JpaRepairerStore(database))
                .withGarageSlotStore(new JpaGarageSlotStore(database))
                .withOrderStore(new JpaOrderStore(database))
                .garageSlotAdditionEnabled(isGarageSlotAdditionEnabled)
                .garageSlotDeletionEnabled(isGarageSlotDeletionEnabled)
                .withClock(clock)
                .build();
        return new TransactionalCarServiceModule(module, database);
    }

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    private void migrate(String jdbcUrl) {
        try (var dataSource = new HikariDataSource()) {
            dataSource.setJdbcUrl(jdbcUrl);
            Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:migrations")
                    .loggers("slf4j")
                    .load()
                    .migrate();
        }
    }
}
