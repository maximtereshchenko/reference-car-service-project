package com.andersenlab.carservice.application.command;

import java.io.PrintStream;
import java.util.List;
import java.util.StringJoiner;

abstract class NamedCommandWithDescription extends NamedCommand {

    NamedCommandWithDescription(String name) {
        super(name);
    }

    @Override
    public void printDescription(PrintStream output) {
        var joiner = new StringJoiner(", ", "(", ") ").setEmptyValue("");
        expectedArguments().forEach(joiner::add);
        output.printf("%s %s- %s%n", name(), joiner, desription());
    }

    abstract List<String> expectedArguments();

    abstract String desription();
}
