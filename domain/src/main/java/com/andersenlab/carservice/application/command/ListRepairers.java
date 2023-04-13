package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.port.usecase.ListRepairersUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

public final class ListRepairers extends NamedCommandWithDescription {

    private final ListRepairersUseCase userCase;

    public ListRepairers(ListRepairersUseCase userCase) {
        super("list");
        this.userCase = userCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("sort");
    }

    @Override
    String description() {
        return "list all known repairers sorted";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        var repairers = userCase.list(
                ListRepairersUseCase.Sort.valueOf(
                        arguments.get(0).toUpperCase(Locale.ROOT)
                )
        );
        output.println("ID, name, status");
        for (int i = 0; i < repairers.size(); i++) {
            var repairer = repairers.get(i);
            output.printf("%d) %s, %s, %s%n", i + 1, repairer.id(), repairer.name(), repairer.status());
        }
    }
}
