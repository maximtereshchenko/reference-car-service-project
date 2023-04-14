package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.ViewOrderUseCase;

import java.io.PrintStream;
import java.util.*;

final class ViewOrder extends NamedCommandWithDescription {

    private static final String NONE = "NONE";

    private final ViewOrderUseCase userCase;

    ViewOrder(ViewOrderUseCase userCase) {
        super("view");
        this.userCase = userCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("id");
    }

    @Override
    String description() {
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
                Repairers: %s
                Created: %S
                Closed: %s
                """,
                order.id(),
                order.price(),
                order.status(),
                order.garageSlotId()
                        .map(Objects::toString)
                        .orElse(NONE),
                repairers(order.repairers()),
                order.created(),
                order.closed()
                        .map(Objects::toString)
                        .orElse(NONE)
        );
    }

    private String repairers(Collection<UUID> repairers) {
        var joiner = new StringJoiner(", ").setEmptyValue(NONE);
        repairers.stream()
                .map(Objects::toString)
                .forEach(joiner::add);
        return joiner.toString();
    }
}
