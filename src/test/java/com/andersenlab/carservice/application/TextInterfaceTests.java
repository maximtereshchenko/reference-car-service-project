package com.andersenlab.carservice.application;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

final class TextInterfaceTests {

    private final StringWriter output = new StringWriter();
    private final TextInterface textInterface = new TextInterface(new PrintWriter(output), new EchoCommand());

    @Test
    void givenEchoCommand_whenExecute_thenArgumentsPrinted() {
        textInterface.execute("echo hello world");

        assertThat(output.toString()).isEqualToIgnoringNewLines("hello world");
    }

    private static final class EchoCommand extends SimpleCommand {

        EchoCommand() {
            super("echo");
        }

        @Override
        public void execute(PrintWriter output, List<String> arguments) {
            output.println(String.join(" ", arguments));
        }
    }
}
