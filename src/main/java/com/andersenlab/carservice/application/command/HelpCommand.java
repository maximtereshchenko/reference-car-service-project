package com.andersenlab.carservice.application.command;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

public final class HelpCommand extends NamedCommandWithDescription {

    private final Collection<Command> commands;

    public HelpCommand(Collection<? extends Command> commands) {
        super("help");
        this.commands = List.copyOf(commands);
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        commands.forEach(command -> command.printDescription(output));
        printDescription(output);
    }

    @Override
    List<String> expectedArguments() {
        return List.of();
    }

    @Override
    String desription() {
        return "print all available commands";
    }
}
