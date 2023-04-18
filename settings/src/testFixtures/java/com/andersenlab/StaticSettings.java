package com.andersenlab;

import java.nio.file.Path;

public record StaticSettings(Path stateFilePath) implements Settings {

    @Override
    public String jdbcUrl() {
        return "jdbc:h2:mem:car_service;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false";
    }

    @Override
    public boolean isGarageSlotAdditionEnabled() {
        return true;
    }

    @Override
    public boolean isGarageSlotDeletionEnabled() {
        return true;
    }
}
