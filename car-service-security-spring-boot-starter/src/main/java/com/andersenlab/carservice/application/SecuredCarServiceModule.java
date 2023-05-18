package com.andersenlab.carservice.application;

import com.andersenlab.carservice.domain.CarServiceModule;
import com.andersenlab.carservice.port.usecase.*;

public class SecuredCarServiceModule implements CarServiceModule {

    private final CarServiceModule original;
    private final SecuredProxy securedProxy;

    public SecuredCarServiceModule(CarServiceModule original, SecuredProxy securedProxy) {
        this.original = original;
        this.securedProxy = securedProxy;
    }

    @Override
    public AddRepairerUseCase addRepairerUseCase() {
        return (id, name) ->
                securedProxy.secured(
                        () -> original.addRepairerUseCase().add(id, name),
                        Role.HUMAN_RESOURCES_SPECIALIST
                );
    }

    @Override
    public ListRepairersUseCase listRepairersUserCase() {
        return sort ->
                securedProxy.secured(
                        () -> original.listRepairersUserCase().list(sort),
                        Role.REGULAR_EMPLOYEE
                );
    }

    @Override
    public DeleteRepairerUseCase deleteRepairerUseCase() {
        return id ->
                securedProxy.secured(
                        () -> original.deleteRepairerUseCase().delete(id),
                        Role.HUMAN_RESOURCES_SPECIALIST
                );
    }

    @Override
    public AddGarageSlotUseCase addGarageSlotUseCase() {
        return id ->
                securedProxy.secured(
                        () -> original.addGarageSlotUseCase().add(id),
                        Role.OPERATIONAL_MANAGER
                );
    }

    @Override
    public ListGarageSlotsUseCase listGarageSlotsUseCase() {
        return sort ->
                securedProxy.secured(
                        () -> original.listGarageSlotsUseCase().list(sort),
                        Role.REGULAR_EMPLOYEE
                );
    }

    @Override
    public DeleteGarageSlotUseCase deleteGarageSlotUseCase() {
        return id ->
                securedProxy.secured(
                        () -> original.deleteGarageSlotUseCase().delete(id),
                        Role.OPERATIONAL_MANAGER
                );
    }

    @Override
    public CreateOrderUseCase createOrderUseCase() {
        return (id, price) ->
                securedProxy.secured(
                        () -> original.createOrderUseCase().create(id, price),
                        Role.SALES_SPECIALIST
                );
    }

    @Override
    public ListOrdersUseCase listOrdersUseCase() {
        return sort ->
                securedProxy.secured(
                        () -> original.listOrdersUseCase().list(sort),
                        Role.SALES_SPECIALIST,
                        Role.OPERATIONAL_MANAGER
                );
    }

    @Override
    public AssignGarageSlotToOrderUseCase assignGarageSlotToOrderUseCase() {
        return (orderId, garageSlotId) ->
                securedProxy.secured(
                        () -> original.assignGarageSlotToOrderUseCase().assignGarageSlot(orderId, garageSlotId),
                        Role.OPERATIONAL_MANAGER
                );
    }

    @Override
    public ViewOrderUseCase viewOrderUseCase() {
        return id ->
                securedProxy.secured(
                        () -> original.viewOrderUseCase().view(id),
                        Role.SALES_SPECIALIST,
                        Role.OPERATIONAL_MANAGER
                );
    }

    @Override
    public AssignRepairerToOrderUseCase assignRepairerToOrderUseCase() {
        return (orderId, repairerId) ->
                securedProxy.secured(
                        () -> original.assignRepairerToOrderUseCase().assignRepairer(orderId, repairerId),
                        Role.OPERATIONAL_MANAGER
                );
    }

    @Override
    public CompleteOrderUseCase completeOrderUseCase() {
        return id ->
                securedProxy.secured(
                        () -> original.completeOrderUseCase().complete(id),
                        Role.OPERATIONAL_MANAGER
                );
    }

    @Override
    public CancelOrderUseCase cancelOrderUseCase() {
        return id ->
                securedProxy.secured(
                        () -> original.cancelOrderUseCase().cancel(id),
                        Role.SALES_SPECIALIST
                );
    }
}
