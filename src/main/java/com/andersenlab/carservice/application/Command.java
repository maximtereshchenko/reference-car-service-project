package com.andersenlab.carservice.application;

import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

interface Command {

    Optional<String> matchedName(String input);

    void execute(PrintWriter output, List<String> arguments);
}
