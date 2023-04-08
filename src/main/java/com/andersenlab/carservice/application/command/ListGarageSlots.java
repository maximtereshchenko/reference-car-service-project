package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.port.usecase.ListGarageSlotsUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

public final class ListGarageSlots extends NamedCommandWithDescription {

    private final ListGarageSlotsUseCase userCase;

    public ListGarageSlots(ListGarageSlotsUseCase userCase) {
        super("list");
        this.userCase = userCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("sort");
    }

    @Override
    String desription() {
        return "list all known garage slots sorted";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        var garageSlots = userCase.list(
                ListGarageSlotsUseCase.Sort.valueOf(
                        arguments.get(0).toUpperCase(Locale.ROOT)
                )
        );
        for (int i = 0; i < garageSlots.size(); i++) {
            var garageSlot = garageSlots.get(i);
            output.printf("%d) %s", i + 1, garageSlot.id());
        }
    }
}
