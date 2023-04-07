package com.andersenlab.carservice.application.command;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

public final class CompositeCommand extends NamedCommand {

    private final Collection<Command> commands;

    public CompositeCommand(String name, Command... commands) {
        super(name);
        this.commands = List.of(commands);
    }

    @Override
    public void printDescription(PrintStream output) {
        for (Command command : commands) {
            output.print(name());
            output.print(' ');
            command.printDescription(output);
        }
    }

    @Override
    void executeIfMatched(PrintStream output, List<String> arguments) {
        commands.forEach(command -> command.execute(output, arguments));
    }
}
