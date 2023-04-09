package com.andersenlab.carservice.application;

import com.andersenlab.carservice.application.command.Command;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public final class TextInterface {

    private final InputStream inputStream;
    private final PrintStream printStream;
    private final Collection<Command> commands;

    public TextInterface(InputStream inputStream, PrintStream printStream, Command... commands) {
        this(inputStream, printStream, List.of(commands));
    }

    public TextInterface(InputStream inputStream, PrintStream printStream, Collection<? extends Command> commands) {
        this.inputStream = inputStream;
        this.printStream = printStream;
        this.commands = List.copyOf(commands);
    }

    public void run() {
        try (var scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            while (true) {
                var input = scanner.nextLine();
                if (input.equals("exit")) {
                    return;
                }
                commands.forEach(command -> command.execute(printStream, List.of(input.trim().split(" "))));
            }
        }
    }
}
