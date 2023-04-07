package com.andersenlab.carservice.application;

import java.util.Optional;

abstract class SimpleCommand implements Command {

    private final String name;

    SimpleCommand(String name) {
        this.name = name;
    }

    @Override
    public Optional<String> matchedName(String input) {
        if (input.startsWith(name)) {
            return Optional.of(name);
        }
        return Optional.empty();
    }
}
