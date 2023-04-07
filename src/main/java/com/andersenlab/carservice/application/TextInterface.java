package com.andersenlab.carservice.application;

import java.io.PrintWriter;
import java.util.List;

public final class TextInterface {

    private final PrintWriter output;
    private final Iterable<Command> commands;

    public TextInterface(PrintWriter output, Command... commands) {
        this.output = output;
        this.commands = List.of(commands);
    }

    public void execute(String input) {
        for (Command command : commands) {
            var matchedName = command.matchedName(input);
            if (matchedName.isEmpty()) {
                continue;
            }
            command.execute(output, arguments(input, matchedName.get()));
            return;
        }
    }

    private List<String> arguments(String input, CharSequence matchedName) {
        return List.of(
                input.substring(matchedName.length())
                        .trim()
                        .split(" ")
        );
    }
}
