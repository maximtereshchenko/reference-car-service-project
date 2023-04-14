package com.andersenlab.carservice.application;

import java.io.PrintStream;
import java.util.List;

interface Command {

    void printDescription(PrintStream output);

    void execute(PrintStream output, List<String> arguments);
}
