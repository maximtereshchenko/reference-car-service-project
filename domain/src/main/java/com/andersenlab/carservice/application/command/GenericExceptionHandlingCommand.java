package com.andersenlab.carservice.application.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.List;

public final class GenericExceptionHandlingCommand implements Command {

    private static final Logger LOG = LoggerFactory.getLogger(GenericExceptionHandlingCommand.class);

    private final Command original;

    public GenericExceptionHandlingCommand(Command original) {
        this.original = original;
    }

    @Override
    public void printDescription(PrintStream output) {
        original.printDescription(output);
    }

    @Override
    public void execute(PrintStream output, List<String> arguments) {
        try {
            original.execute(output, arguments);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            LOG.warn("Caught generic exception", e);
            output.println("Wrong arguments");
        }
    }
}
