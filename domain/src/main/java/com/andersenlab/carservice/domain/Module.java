package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.external.RepairerStore;
import com.andersenlab.carservice.port.usecase.*;

import java.time.Clock;

public final class Module implements CarServiceModule {

    private final RepairerService repairerService;
    private final GarageSlotService garageSlotService;
    private final OrderService orderService;
    private final boolean isGarageSlotAdditionEnabled;
    private final boolean isGarageSlotDeletionEnabled;

    private Module(
            RepairerService repairerService,
            GarageSlotService garageSlotService,
            OrderService orderService,
            boolean isGarageSlotAdditionEnabled,
            boolean isGarageSlotDeletionEnabled
    ) {
        this.repairerService = repairerService;
        this.garageSlotService = garageSlotService;
        this.orderService = orderService;
        this.isGarageSlotAdditionEnabled = isGarageSlotAdditionEnabled;
        this.isGarageSlotDeletionEnabled = isGarageSlotDeletionEnabled;
    }

    @Override
    public AddRepairerUseCase addRepairerUseCase() {
        return repairerService;
    }

    @Override
    public ListRepairersUseCase listRepairersUserCase() {
        return repairerService;
    }

    @Override
    public DeleteRepairerUseCase deleteRepairerUseCase() {
        return repairerService;
    }

    @Override
    public AddGarageSlotUseCase addGarageSlotUseCase() {
        if (isGarageSlotAdditionEnabled) {
            return garageSlotService;
        }
        return new DisabledAddGarageSlotUseCase();
    }

    @Override
    public ListGarageSlotsUseCase listGarageSlotsUseCase() {
        return garageSlotService;
    }

    @Override
    public DeleteGarageSlotUseCase deleteGarageSlotUseCase() {
        if (isGarageSlotDeletionEnabled) {
            return garageSlotService;
        }
        return new DisabledDeleteGarageSlotUseCase();
    }

    @Override
    public CreateOrderUseCase createOrderUseCase() {
        return orderService;
    }

    @Override
    public ListOrdersUseCase listOrdersUseCase() {
        return orderService;
    }

    @Override
    public AssignGarageSlotToOrderUseCase assignGarageSlotToOrderUseCase() {
        return orderService;
    }

    @Override
    public ViewOrderUseCase viewOrderUseCase() {
        return orderService;
    }

    @Override
    public AssignRepairerToOrderUseCase assignRepairerToOrderUseCase() {
        return orderService;
    }

    @Override
    public CompleteOrderUseCase completeOrderUseCase() {
        return orderService;
    }

    @Override
    public CancelOrderUseCase cancelOrderUseCase() {
        return orderService;
    }

    public static final class Builder {

        private RepairerStore repairerStore;
        private GarageSlotStore garageSlotStore;
        private OrderStore orderStore;
        private Clock clock;
        private boolean isGarageSlotAdditionEnabled = true;
        private boolean isGarageSlotDeletionEnabled = true;

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

        public Builder garageSlotAdditionEnabled(boolean isGarageSlotAdditionEnabled) {
            this.isGarageSlotAdditionEnabled = isGarageSlotAdditionEnabled;
            return this;
        }

        public Builder garageSlotDeletionEnabled(boolean isGarageSlotDeletionEnabled) {
            this.isGarageSlotDeletionEnabled = isGarageSlotDeletionEnabled;
            return this;
        }

        public CarServiceModule build() {
            var repairerService = new RepairerService(repairerStore);
            var garageSlotService = new GarageSlotService(garageSlotStore);
            return new Module(
                    repairerService,
                    garageSlotService,
                    new OrderService(orderStore, garageSlotService, repairerService, clock, new OrderFactory()),
                    isGarageSlotAdditionEnabled,
                    isGarageSlotDeletionEnabled
            );
        }
    }
}
