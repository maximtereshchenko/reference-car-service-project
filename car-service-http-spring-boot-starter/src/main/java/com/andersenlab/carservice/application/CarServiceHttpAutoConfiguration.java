package com.andersenlab.carservice.application;

import com.andersenlab.carservice.domain.CarServiceModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
class CarServiceHttpAutoConfiguration {

    @Bean
    RepairersController repairerController(CarServiceModule module) {
        return new RepairersController(
                module.listRepairersUserCase(),
                module.addRepairerUseCase(),
                module.deleteRepairerUseCase()
        );
    }

    @Bean
    GarageSlotsController garageSlotsController(CarServiceModule module) {
        return new GarageSlotsController(
                module.listGarageSlotsUseCase(),
                module.addGarageSlotUseCase(),
                module.deleteGarageSlotUseCase()
        );
    }

    @Bean
    OrdersController ordersController(CarServiceModule module) {
        return new OrdersController(
                module.listOrdersUseCase(),
                module.viewOrderUseCase(),
                module.createOrderUseCase(),
                module.cancelOrderUseCase(),
                module.assignGarageSlotToOrderUseCase(),
                module.assignRepairerToOrderUseCase(),
                module.completeOrderUseCase()
        );
    }
}
