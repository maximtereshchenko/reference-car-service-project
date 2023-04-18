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
        return (id, name) -> database.transactional(() -> original.addRepairerUseCase().add(id, name));
    }

    @Override
    public ListRepairersUseCase listRepairersUserCase() {
        return sort -> database.transactional(() -> original.listRepairersUserCase().list(sort));
    }

    @Override
    public DeleteRepairerUseCase deleteRepairerUseCase() {
        return id -> database.transactional(() -> original.deleteRepairerUseCase().delete(id));
    }

    @Override
    public AddGarageSlotUseCase addGarageSlotUseCase() {
        return id -> database.transactional(() -> original.addGarageSlotUseCase().add(id));
    }

    @Override
    public ListGarageSlotsUseCase listGarageSlotsUseCase() {
        return sort -> database.transactional(() -> original.listGarageSlotsUseCase().list(sort));
    }

    @Override
    public DeleteGarageSlotUseCase deleteGarageSlotUseCase() {
        return id -> database.transactional(() -> original.deleteGarageSlotUseCase().delete(id));
    }

    @Override
    public CreateOrderUseCase createOrderUseCase() {
        return (id, price) -> database.transactional(() -> original.createOrderUseCase().create(id, price));
    }

    @Override
    public ListOrdersUseCase listOrdersUseCase() {
        return sort -> database.transactional(() -> original.listOrdersUseCase().list(sort));
    }

    @Override
    public AssignGarageSlotToOrderUseCase assignGarageSlotToOrderUseCase() {
        return (orderId, garageSlotId) ->
                database.transactional(() ->
                        original.assignGarageSlotToOrderUseCase()
                                .assignGarageSlot(orderId, garageSlotId)
                );
    }

    @Override
    public ViewOrderUseCase viewOrderUseCase() {
        return id -> database.transactional(() -> original.viewOrderUseCase().view(id));
    }

    @Override
    public AssignRepairerToOrderUseCase assignRepairerToOrderUseCase() {
        return (orderId, repairerId) ->
                database.transactional(() ->
                        original.assignRepairerToOrderUseCase()
                                .assignRepairer(orderId, repairerId)
                );
    }

    @Override
    public CompleteOrderUseCase completeOrderUseCase() {
        return id -> database.transactional(() -> original.completeOrderUseCase().complete(id));
    }

    @Override
    public CancelOrderUseCase cancelOrderUseCase() {
        return id -> database.transactional(() -> original.cancelOrderUseCase().cancel(id));
    }
}
