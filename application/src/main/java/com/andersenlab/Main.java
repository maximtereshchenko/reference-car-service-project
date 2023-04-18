package com.andersenlab;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.Objects;

final class Main {

    public static void main(String[] args) throws URISyntaxException {
        Application.from(settings(), Clock.systemDefaultZone()).run();
    }

    private static Settings settings() throws URISyntaxException {
        return TomlSettings.from(
                Paths.get(
                        Objects.requireNonNull(
                                        Thread.currentThread()
                                                .getContextClassLoader()
                                                .getResource("application.toml")
                                )
                                .toURI()
                )
        );
    }
}
