package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.port.usecase.AddRepairerUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

public final class AddRepairer extends NamedCommandWithDescription {

    private final AddRepairerUseCase useCase;

    public AddRepairer(AddRepairerUseCase useCase) {
        super("add");
        this.useCase = useCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("name");
    }

    @Override
    String desription() {
        return "add a repairer with given name";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        var id = UUID.randomUUID();
        useCase.add(id, arguments.get(0));
        output.println("Repairer added " + id);
    }
}
