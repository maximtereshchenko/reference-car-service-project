package com.andersenlab;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

public final class TomlSettings implements Settings {

    private static final String STATE_FILE_PATH_PROPERTY = "application.stateFile.path";
    private static final String GARAGE_SLOT_ADDITION_ENABLED_PROPERTY = "application.garageSlots.addition.enabled";
    private static final String GARAGE_SLOT_DELETION_ENABLED_PROPERTY = "application.garageSlots.deletion.enabled";

    private final TomlTable table;

    private TomlSettings(TomlTable table) {
        this.table = table;
    }

    public static Settings from(Path path) {
        try {
            var result = Toml.parse(path);
            if (result.hasErrors()) {
                throwWithCombinedErrorMessage(result);
            }
            if (hasNotAllRequiredProperties(result)) {
                throw new IllegalArgumentException("Not all required properties set");
            }
            return new TomlSettings(result);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void throwWithCombinedErrorMessage(TomlParseResult result) {
        var combined = result.errors()
                .stream()
                .map(Objects::toString)
                .collect(Collectors.joining(System.lineSeparator()));
        throw new IllegalArgumentException(combined);
    }

    private static boolean hasNotAllRequiredProperties(TomlTable tomlTable) {
        return !(
                tomlTable.contains(STATE_FILE_PATH_PROPERTY) &&
                        tomlTable.contains(GARAGE_SLOT_ADDITION_ENABLED_PROPERTY) &&
                        tomlTable.contains(GARAGE_SLOT_DELETION_ENABLED_PROPERTY)
        );
    }

    @Override
    public Path stateFilePath() {
        return Paths.get(Objects.requireNonNull(table.getString(STATE_FILE_PATH_PROPERTY)));
    }

    @Override
    public boolean isGarageSlotAdditionEnabled() {
        return Boolean.TRUE.equals(table.getBoolean(GARAGE_SLOT_ADDITION_ENABLED_PROPERTY));
    }

    @Override
    public boolean isGarageSlotDeletionEnabled() {
        return Boolean.TRUE.equals(table.getBoolean(GARAGE_SLOT_DELETION_ENABLED_PROPERTY));
    }
}
