package com.andersenlab.carservice.application.command;

import java.io.PrintStream;
import java.util.List;

public interface Command {

    void printDescription(PrintStream output);

    void execute(PrintStream output, List<String> arguments);
}
