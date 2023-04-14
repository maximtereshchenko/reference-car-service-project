package com.andersenlab.carservice.application;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

final class TextInterfaceTests {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Test
    void givenEchoCommand_whenExecute_thenArgumentsPrinted() {
        var textInterface = new TextInterface(
                input(
                        """
                        echo hello world
                        exit
                        """
                ),
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new EchoCommand()
        );

        textInterface.run();

        assertThat(output.toString()).contains("hello world");
    }

    @Test
    void givenCompositeCommand_whenExecute_thenArgumentsPrinted() {
        var textInterface = new TextInterface(
                input(
                        """
                        composite echo hello world
                        exit
                        """
                ),
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new PrefixedCommand("composite", new EchoCommand())
        );

        textInterface.run();

        assertThat(output.toString()).contains("hello world");
    }

    private InputStream input(String commands) {
        return new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    }

    private static final class EchoCommand implements Command {

        @Override
        public void printDescription(PrintStream output) {
            // empty
        }

        @Override
        public void execute(PrintStream output, List<String> arguments) {
            if (!arguments.get(0).equals("echo")) {
                return;
            }
            output.println(String.join(" ", arguments.subList(1, arguments.size())));
        }
    }
}
