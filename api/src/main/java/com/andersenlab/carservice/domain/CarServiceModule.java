package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.usecase.*;

public interface CarServiceModule {

    AddRepairerUseCase addRepairerUseCase();

    ListRepairersUseCase listRepairersUserCase();

    DeleteRepairerUseCase deleteRepairerUseCase();

    AddGarageSlotUseCase addGarageSlotUseCase();

    ListGarageSlotsUseCase listGarageSlotsUseCase();

    DeleteGarageSlotUseCase deleteGarageSlotUseCase();

    CreateOrderUseCase createOrderUseCase();

    ListOrdersUseCase listOrdersUseCase();

    AssignGarageSlotToOrderUseCase assignGarageSlotToOrderUseCase();

    ViewOrderUseCase viewOrderUseCase();

    AssignRepairerToOrderUseCase assignRepairerToOrderUseCase();

    CompleteOrderUseCase completeOrderUseCase();

    CancelOrderUseCase cancelOrderUseCase();
}
