package com.andersenlab.carservice.application;

import java.io.PrintStream;
import java.util.List;

final class PrefixedCommand extends NamedCommand {

    private final Command original;

    PrefixedCommand(String prefix, Command original) {
        super(prefix);
        this.original = original;
    }

    @Override
    public void printDescription(PrintStream output) {
        output.print(name());
        output.print(' ');
        original.printDescription(output);
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        original.execute(output, arguments);
    }
}
