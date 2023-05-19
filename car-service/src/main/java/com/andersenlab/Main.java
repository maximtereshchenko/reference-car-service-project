package com.andersenlab;

import com.andersenlab.carservice.application.SecuredCarServiceModule;
import com.andersenlab.carservice.application.SecuredProxy;
import com.andersenlab.carservice.application.storage.spring.jpa.TransactionalCarServiceModule;
import com.andersenlab.carservice.domain.CarServiceModule;
import com.andersenlab.carservice.domain.Module;
import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.external.MessageBroker;
import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.external.RepairerStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;

@SpringBootConfiguration
@EnableAutoConfiguration
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }

    @Bean
    CarServiceModule transactionalCarServiceModule(
            RepairerStore repairerStore,
            GarageSlotStore garageSlotStore,
            OrderStore orderStore,
            @Value("${car-service.garageSlots.addition.enabled}") boolean isGarageSlotAdditionEnabled,
            @Value("${car-service.garageSlots.deletion.enabled}") boolean isGarageSlotDeletionEnabled,
            Clock clock,
            MessageBroker messageBroker
    ) {
        return new TransactionalCarServiceModule(
                new Module.Builder()
                        .withRepairerStore(repairerStore)
                        .withGarageSlotStore(garageSlotStore)
                        .withOrderStore(orderStore)
                        .garageSlotAdditionEnabled(isGarageSlotAdditionEnabled)
                        .garageSlotDeletionEnabled(isGarageSlotDeletionEnabled)
                        .withClock(clock)
                        .withMessageBroker(messageBroker)
                        .build()
        );
    }

    @Bean
    @Primary
    CarServiceModule securedCarServiceModule(
            CarServiceModule transactionalCarServiceModule,
            SecuredProxy securedProxy
    ) {
        return new SecuredCarServiceModule(transactionalCarServiceModule, securedProxy);
    }

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    FlywayConfigurationCustomizer slf4jLoggerCustomizer() {
        return configuration -> configuration.loggers("slf4j");
    }
}
