package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.port.usecase.DeleteGarageSlotUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

public final class DeleteGarageSlot extends NamedCommandWithDescription {

    private final DeleteGarageSlotUseCase useCase;

    public DeleteGarageSlot(DeleteGarageSlotUseCase useCase) {
        super("delete");
        this.useCase = useCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("id");
    }

    @Override
    String description() {
        return "delete a garage slot with given ID";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        useCase.delete(UUID.fromString(arguments.get(0)));
        output.println("Garage slot deleted");
    }
}
