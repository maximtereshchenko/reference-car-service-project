package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.port.usecase.CreateOrderUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

public final class CreateOrder extends NamedCommandWithDescription {

    private final CreateOrderUseCase useCase;

    public CreateOrder(CreateOrderUseCase useCase) {
        super("create");
        this.useCase = useCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("id?", "price");
    }

    @Override
    String description() {
        return "create an order with given price and, optionally, ID";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        var id = id(arguments);
        useCase.create(id, Long.parseLong(price(arguments)));
        output.println("Order created " + id);
    }

    private String price(List<String> arguments) {
        if (arguments.size() == 1) {
            return arguments.get(0);
        }
        return arguments.get(1);
    }

    private UUID id(List<String> arguments) {
        if (arguments.size() == 1) {
            return UUID.randomUUID();
        }
        return UUID.fromString(arguments.get(0));
    }
}
