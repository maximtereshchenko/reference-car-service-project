package com.andersenlab.carservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.SAME_THREAD)
final class EndToEndTests {

    private static final String[] ARGS = {};

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(output));
    }

    @Test
    void help() {
        input("""
                help
                exit
                """);

        Main.main(ARGS);

        assertThat(output.toString())
                .isEqualTo("""
                        repairers add (name) - add a repairer with given name
                        repairers list (sort) - list all known repairers sorted
                        help - print all available commands
                        """);
    }

    @Test
    void addRepairer() {
        input("""
                repairers add John
                exit
                """);

        Main.main(ARGS);

        assertThat(output.toString()).contains("Repairer added");
    }

    @Test
    void listRepairers() {
        input("""
                repairers add John
                repairers list name
                exit
                """);

        Main.main(ARGS);

        assertThat(output.toString()).contains("Repairer added", "1) ", ", John");
    }

    private void input(String commands) {
        System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
    }
}
