package com.andersenlab;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SettingsTests {

    @Test
    void givenFileExists_whenReadSettings_thenExpectedSettingsReturned() {
        var settings = TomlSettings.from(Paths.get("src/test/resources/test.toml"));

        assertThat(settings.jdbcUrl()).isEqualTo("url");
        assertThat(settings.isGarageSlotAdditionEnabled()).isFalse();
        assertThat(settings.isGarageSlotDeletionEnabled()).isTrue();
    }

    @Test
    void givenFileDoNotExist_whenCreateSettings_thenIllegalArgumentExceptionThrown() {
        var path = Paths.get("non-existent-file");

        assertThatThrownBy(() -> TomlSettings.from(path)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenFileContainsErrors_whenCreateSettings_thenIllegalArgumentExceptionThrown() {
        var path = Paths.get("src/test/resources/unreadable.toml");

        assertThatThrownBy(() -> TomlSettings.from(path)).isInstanceOf(IllegalArgumentException.class);
    }
}