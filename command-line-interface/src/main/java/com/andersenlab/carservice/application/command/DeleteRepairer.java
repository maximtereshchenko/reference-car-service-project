package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.port.usecase.DeleteRepairerUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

public final class DeleteRepairer extends NamedCommandWithDescription {

    private final DeleteRepairerUseCase useCase;

    public DeleteRepairer(DeleteRepairerUseCase useCase) {
        super("delete");
        this.useCase = useCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("id");
    }

    @Override
    String description() {
        return "delete a repairer with given ID";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        var id = UUID.fromString(arguments.get(0));
        useCase.delete(id);
        output.println("Repairer deleted " + id);
    }
}
