package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.CancelOrderUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

final class CancelOrder extends NamedCommandWithDescription {

    private final CancelOrderUseCase useCase;

    CancelOrder(CancelOrderUseCase useCase) {
        super("cancel");
        this.useCase = useCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("id");
    }

    @Override
    String description() {
        return "cancel an order";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        var id = UUID.fromString(arguments.get(0));
        useCase.cancel(id);
        output.println("Order canceled " + id);
    }
}
