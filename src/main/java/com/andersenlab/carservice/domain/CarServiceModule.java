package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.external.RepairerStore;
import com.andersenlab.carservice.port.usecase.*;

import java.time.Clock;

public final class CarServiceModule {

    private final RepairerService repairerService;
    private final GarageSlotService garageSlotService;
    private final OrderService orderService;

    public CarServiceModule(
            RepairerStore repairerStore,
            GarageSlotStore garageSlotStore,
            OrderStore orderStore,
            Clock clock
    ) {
        repairerService = new RepairerService(repairerStore);
        garageSlotService = new GarageSlotService(garageSlotStore);
        orderService = new OrderService(orderStore, garageSlotStore, repairerStore, clock);
    }

    public AddRepairerUseCase addRepairerUseCase() {
        return repairerService;
    }

    public ListRepairersUseCase listRepairersUserCase() {
        return repairerService;
    }

    public DeleteRepairerUseCase deleteRepairerUseCase() {
        return repairerService;
    }

    public AddGarageSlotUseCase addGarageSlotUseCase() {
        return garageSlotService;
    }

    public ListGarageSlotsUseCase listGarageSlotsUseCase() {
        return garageSlotService;
    }

    public DeleteGarageSlotUseCase deleteGarageSlotUseCase() {
        return garageSlotService;
    }

    public CreateOrderUseCase createOrderUseCase() {
        return orderService;
    }

    public ListOrdersUseCase listOrdersUseCase() {
        return orderService;
    }

    public AssignGarageSlotToOrderUseCase assignGarageSlotToOrderUseCase() {
        return orderService;
    }

    public ViewOrderUseCase viewOrderUseCase() {
        return orderService;
    }

    public AssignRepairerToOrderUseCase assignRepairerToOrderUseCase() {
        return orderService;
    }

    public CompleteOrderUseCase completeOrderUseCase() {
        return orderService;
    }
}
