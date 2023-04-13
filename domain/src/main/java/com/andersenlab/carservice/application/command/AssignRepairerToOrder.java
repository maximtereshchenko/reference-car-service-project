package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.port.usecase.AssignRepairerToOrderUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

public final class AssignRepairerToOrder extends NamedCommandWithDescription {

    private final AssignRepairerToOrderUseCase useCase;

    public AssignRepairerToOrder(AssignRepairerToOrderUseCase useCase) {
        super("repairer");
        this.useCase = useCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("order id", "repairer id");
    }

    @Override
    String description() {
        return "assign a repairer to the order";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        var orderId = UUID.fromString(arguments.get(0));
        var repairerId = UUID.fromString(arguments.get(1));
        useCase.assignRepairer(orderId, repairerId);
        output.println("Repairer " + repairerId + " assigned to order " + orderId);
    }
}
