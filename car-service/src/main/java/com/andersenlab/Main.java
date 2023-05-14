package com.andersenlab;

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
            RepairerStore repairerStore,
            GarageSlotStore garageSlotStore,
            OrderStore orderStore,
            @Value("${car-service.garageSlots.addition.enabled}") boolean isGarageSlotAdditionEnabled,
            @Value("${car-service.garageSlots.deletion.enabled}") boolean isGarageSlotDeletionEnabled,
            Clock clock,
            MessageBroker messageBroker
    ) {
        return new Module.Builder()
                .withRepairerStore(repairerStore)
                .withGarageSlotStore(garageSlotStore)
                .withOrderStore(orderStore)
                .garageSlotAdditionEnabled(isGarageSlotAdditionEnabled)
                .garageSlotDeletionEnabled(isGarageSlotDeletionEnabled)
                .withClock(clock)
                .withMessageBroker(messageBroker)
                .build();
    }

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}