package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.application.TextInterface;
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
                input("""
                        echo hello world
                        exit
                        """),
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new EchoCommand()
        );

        textInterface.run();

        assertThat(output.toString()).isEqualToIgnoringNewLines("hello world");
    }

    @Test
    void givenCompositeCommand_whenExecute_thenArgumentsPrinted() {
        var textInterface = new TextInterface(
                input("""
                        composite echo hello world
                        exit
                        """),
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new CompositeCommand("composite", new EchoCommand())
        );

        textInterface.run();

        assertThat(output.toString()).isEqualToIgnoringNewLines("hello world");
    }

    private InputStream input(String commands) {
        return new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    }

    private static final class EchoCommand extends NamedCommand {

        EchoCommand() {
            super("echo");
        }

        @Override
        public void executeIfMatched(PrintStream output, List<String> arguments) {
            output.println(String.join(" ", arguments));
        }

        @Override
        public void printDescription(PrintStream output) {
            // empty
        }
    }
}
