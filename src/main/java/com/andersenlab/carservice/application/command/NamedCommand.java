package com.andersenlab.carservice.application.command;

import java.io.PrintStream;
import java.util.List;

abstract class NamedCommand implements Command {

    private final String name;

    NamedCommand(String name) {
        this.name = name;
    }

    @Override
    public void execute(PrintStream output, List<String> arguments) {
        if (arguments.isEmpty() || !arguments.get(0).equals(name)) {
            return;
        }
        executeIfMatched(output, arguments.subList(1, arguments.size()));
    }

    String name() {
        return name;
    }

    abstract void executeIfMatched(PrintStream output, List<String> arguments);
}
