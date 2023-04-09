package com.andersenlab.carservice.application.command;

import java.io.PrintStream;
import java.util.List;

public final class PrefixedCommand extends NamedCommand {

    private final Command original;

    public PrefixedCommand(String prefix, Command original) {
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
