package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.port.usecase.ListOrdersUseCase;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class ListOrders extends NamedCommandWithDescription {

    private final ListOrdersUseCase userCase;

    public ListOrders(ListOrdersUseCase userCase) {
        super("list");
        this.userCase = userCase;
    }

    @Override
    List<String> expectedArguments() {
        return List.of("sort");
    }

    @Override
    String description() {
        return "list all known orders sorted";
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        var orders = userCase.list(
                ListOrdersUseCase.Sort.valueOf(
                        arguments.get(0).toUpperCase(Locale.ROOT)
                )
        );
        for (int i = 0; i < orders.size(); i++) {
            var order = orders.get(i);
            output.printf(
                    "%d) %s, %d, %s, %s, %s",
                    i + 1,
                    order.id(),
                    order.price(),
                    order.status(),
                    order.created(),
                    order.closed()
                            .map(Objects::toString)
                            .orElse("NONE")
            );
        }
    }
}
