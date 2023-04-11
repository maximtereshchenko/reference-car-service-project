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

    private CarServiceModule(
            RepairerService repairerService,
            GarageSlotService garageSlotService,
            OrderService orderService
    ) {
        this.repairerService = repairerService;
        this.garageSlotService = garageSlotService;
        this.orderService = orderService;
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

    public CancelOrderUseCase cancelOrderUseCase() {
        return orderService;
    }

    public static final class Builder {

        private RepairerStore repairerStore;
        private GarageSlotStore garageSlotStore;
        private OrderStore orderStore;
        private Clock clock;

        public Builder withRepairerStore(RepairerStore repairerStore) {
            this.repairerStore = repairerStore;
            return this;
        }

        public Builder withGarageSlotStore(GarageSlotStore garageSlotStore) {
            this.garageSlotStore = garageSlotStore;
            return this;
        }

        public Builder withOrderStore(OrderStore orderStore) {
            this.orderStore = orderStore;
            return this;
        }

        public Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public CarServiceModule build() {
            var repairerService = new RepairerService(repairerStore);
            var garageSlotService = new GarageSlotService(garageSlotStore);
            return new CarServiceModule(
                    repairerService,
                    garageSlotService,
                    new OrderService(orderStore, garageSlotService, repairerService, clock, new OrderFactory())
            );
        }
    }
}
