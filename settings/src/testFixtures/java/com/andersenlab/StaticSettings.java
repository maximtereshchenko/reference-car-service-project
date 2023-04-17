package com.andersenlab;

import java.nio.file.Path;

public record StaticSettings(Path stateFilePath) implements Settings {

    @Override
    public boolean isGarageSlotAdditionEnabled() {
        return true;
    }

    @Override
    public boolean isGarageSlotDeletionEnabled() {
        return true;
    }
}
