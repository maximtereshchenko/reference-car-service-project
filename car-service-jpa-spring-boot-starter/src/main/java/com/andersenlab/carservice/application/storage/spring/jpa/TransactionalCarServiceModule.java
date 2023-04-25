package com.andersenlab.carservice.application.storage.spring.jpa;

import com.andersenlab.carservice.domain.CarServiceModule;
import com.andersenlab.carservice.port.usecase.*;
import org.springframework.transaction.annotation.Transactional;

class TransactionalCarServiceModule implements CarServiceModule {

    private final CarServiceModule original;

    TransactionalCarServiceModule(CarServiceModule original) {
        this.original = original;
    }

    @Transactional
    @Override
    public AddRepairerUseCase addRepairerUseCase() {
        return original.addRepairerUseCase();
    }

    @Transactional(readOnly = true)
    @Override
    public ListRepairersUseCase listRepairersUserCase() {
        return original.listRepairersUserCase();
    }

    @Transactional
    @Override
    public DeleteRepairerUseCase deleteRepairerUseCase() {
        return original.deleteRepairerUseCase();
    }

    @Transactional
    @Override
    public AddGarageSlotUseCase addGarageSlotUseCase() {
        return original.addGarageSlotUseCase();
    }

    @Transactional(readOnly = true)
    @Override
    public ListGarageSlotsUseCase listGarageSlotsUseCase() {
        return original.listGarageSlotsUseCase();
    }

    @Transactional
    @Override
    public DeleteGarageSlotUseCase deleteGarageSlotUseCase() {
        return original.deleteGarageSlotUseCase();
    }

    @Transactional
    @Override
    public CreateOrderUseCase createOrderUseCase() {
        return original.createOrderUseCase();
    }

    @Transactional(readOnly = true)
    @Override
    public ListOrdersUseCase listOrdersUseCase() {
        return original.listOrdersUseCase();
    }

    @Transactional
    @Override
    public AssignGarageSlotToOrderUseCase assignGarageSlotToOrderUseCase() {
        return original.assignGarageSlotToOrderUseCase();
    }

    @Transactional(readOnly = true)
    @Override
    public ViewOrderUseCase viewOrderUseCase() {
        return original.viewOrderUseCase();
    }

    @Transactional
    @Override
    public AssignRepairerToOrderUseCase assignRepairerToOrderUseCase() {
        return original.assignRepairerToOrderUseCase();
    }

    @Transactional
    @Override
    public CompleteOrderUseCase completeOrderUseCase() {
        return original.completeOrderUseCase();
    }

    @Transactional
    @Override
    public CancelOrderUseCase cancelOrderUseCase() {
        return original.cancelOrderUseCase();
    }
}
