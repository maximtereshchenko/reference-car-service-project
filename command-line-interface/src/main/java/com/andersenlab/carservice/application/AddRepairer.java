package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.AddRepairerUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

final class AddRepairer extends NamedCommandWithDescription {

    private final AddRepairerUseCase useCase;

    AddRepairer(AddRepairerUseCase useCase) {
        super("add");
        this.useCase = useCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("id?", "name");
    }

    @Override
    String description() {
        return "add a repairer with given name and, optionally, ID";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        var id = id(arguments);
        useCase.add(id, name(arguments));
        output.println("Repairer added " + id);
    }

    private String name(List<String> arguments) {
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
