package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.AssignGarageSlotToOrderUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

final class AssignGarageSlotToOrder extends NamedCommandWithDescription {

    private final AssignGarageSlotToOrderUseCase useCase;

    AssignGarageSlotToOrder(AssignGarageSlotToOrderUseCase useCase) {
        super("garage-slot");
        this.useCase = useCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("order id", "garage slot id");
    }

    @Override
    String description() {
        return "assign a garage slot to the order";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        var orderId = UUID.fromString(arguments.get(0));
        var garageSlotId = UUID.fromString(arguments.get(1));
        useCase.assignGarageSlot(orderId, garageSlotId);
        output.println("Garage slot " + garageSlotId + " assigned to order " + orderId);
    }
}
