package com.andersenlab;

import java.util.UUID;

public final class StaticSettings implements Settings {

    private final String databaseName = UUID.randomUUID().toString();

    @Override
    public String jdbcUrl() {
        return "jdbc:h2:mem:" + databaseName;
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
