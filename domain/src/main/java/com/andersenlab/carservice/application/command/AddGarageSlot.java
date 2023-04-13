package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.port.usecase.AddGarageSlotUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

public final class AddGarageSlot extends NamedCommandWithDescription {

    private final AddGarageSlotUseCase useCase;

    public AddGarageSlot(AddGarageSlotUseCase useCase) {
        super("add");
        this.useCase = useCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("id?");
    }

    @Override
    String description() {
        return "add a garage slot with, optionally, given ID";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        var id = id(arguments);
        useCase.add(id);
        output.println("Garage slot added " + id);
    }

    private UUID id(List<String> arguments) {
        if (arguments.isEmpty()) {
            return UUID.randomUUID();
        }
        return UUID.fromString(arguments.get(0));
    }
}
