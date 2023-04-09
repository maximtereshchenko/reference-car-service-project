package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.port.usecase.ViewOrderUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ViewOrder extends NamedCommandWithDescription {

    private static final String NONE = "NONE";

    private final ViewOrderUseCase userCase;

    public ViewOrder(ViewOrderUseCase userCase) {
        super("view");
        this.userCase = userCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("id");
    }

    @Override
    String desription() {
        return "View full order information";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        var order = userCase.view(UUID.fromString(arguments.get(0)));
        output.printf(
                """
                        ID: %s
                        Price: %s
                        Status: %s
                        Garage slot: %s
                        Created: %S
                        Closed: %s
                        """,
                order.id(),
                order.price(),
                order.status(),
                order.garageSlotId()
                        .map(Objects::toString)
                        .orElse(NONE),
                order.created(),
                order.closed()
                        .map(Objects::toString)
                        .orElse(NONE)
        );
    }
}
