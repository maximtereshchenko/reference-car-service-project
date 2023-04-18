package com.andersenlab.carservice.application.storage;

import com.andersenlab.carservice.domain.CarServiceModule;
import com.andersenlab.carservice.port.usecase.*;

public final class TransactionalCarServiceModule implements CarServiceModule {

    private final CarServiceModule original;
    private final Database database;

    public TransactionalCarServiceModule(CarServiceModule original, Database database) {
        this.original = original;
        this.database = database;
    }

    @Override
    public AddRepairerUseCase addRepairerUseCase() {
        return original.addRepairerUseCase();
    }

    @Override
    public ListRepairersUseCase listRepairersUserCase() {
        return original.listRepairersUserCase();
    }

    @Override
    public DeleteRepairerUseCase deleteRepairerUseCase() {
        return original.deleteRepairerUseCase();
    }

    @Override
    public AddGarageSlotUseCase addGarageSlotUseCase() {
        return id -> database.transactionally(() -> original.addGarageSlotUseCase().add(id));
    }

    @Override
    public ListGarageSlotsUseCase listGarageSlotsUseCase() {
        return sort -> database.transactionally(() -> original.listGarageSlotsUseCase().list(sort));
    }

    @Override
    public DeleteGarageSlotUseCase deleteGarageSlotUseCase() {
        return id -> database.transactionally(() -> original.deleteGarageSlotUseCase().delete(id));
    }

    @Override
    public CreateOrderUseCase createOrderUseCase() {
        return original.createOrderUseCase();
    }

    @Override
    public ListOrdersUseCase listOrdersUseCase() {
        return original.listOrdersUseCase();
    }

    @Override
    public AssignGarageSlotToOrderUseCase assignGarageSlotToOrderUseCase() {
        return (orderId, garageSlotId) ->
                database.transactionally(() ->
                        original.assignGarageSlotToOrderUseCase()
                                .assignGarageSlot(orderId, garageSlotId)
                );
    }

    @Override
    public ViewOrderUseCase viewOrderUseCase() {
        return original.viewOrderUseCase();
    }

    @Override
    public AssignRepairerToOrderUseCase assignRepairerToOrderUseCase() {
        return original.assignRepairerToOrderUseCase();
    }

    @Override
    public CompleteOrderUseCase completeOrderUseCase() {
        return id -> database.transactionally(() -> original.completeOrderUseCase().complete(id));
    }

    @Override
    public CancelOrderUseCase cancelOrderUseCase() {
        return id -> database.transactionally(() -> original.cancelOrderUseCase().cancel(id));
    }
}
