package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.port.usecase.CompleteOrderUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

public final class CompleteOrder extends NamedCommandWithDescription {

    private final CompleteOrderUseCase useCase;

    public CompleteOrder(CompleteOrderUseCase useCase) {
        super("complete");
        this.useCase = useCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("id");
    }

    @Override
    String description() {
        return "complete an order";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        useCase.complete(UUID.fromString(arguments.get(0)));
        output.println("Order completed");
    }
}
