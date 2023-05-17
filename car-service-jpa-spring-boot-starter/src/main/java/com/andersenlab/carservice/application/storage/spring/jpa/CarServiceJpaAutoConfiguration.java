package com.andersenlab.carservice.application.storage.spring.jpa;

import com.andersenlab.carservice.application.storage.jpa.RepairerJpaEntity;
import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.external.RepairerStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EntityScan(basePackageClasses = RepairerJpaEntity.class)
class CarServiceJpaAutoConfiguration {

    @Bean
    RepairerStore repairerStore(RepairerJpaEntityRepository repository) {
        return new SpringDataJpaRepairerStore(repository);
    }

    @Bean
    GarageSlotStore garageSlotStore(GarageSlotJpaEntityRepository repository) {
        return new SpringDataJpaGarageSlotStore(repository);
    }

    @Bean
    OrderStore orderStore(OrderJpaEntityRepository repository) {
        return new SpringDataJpaOrderStore(repository);
    }
}
