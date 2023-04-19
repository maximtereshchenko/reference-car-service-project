package com.andersenlab;

import java.util.UUID;

public final class StaticSettings implements Settings {

    @Override
    public String jdbcUrl() {
        return "jdbc:h2:mem:" + UUID.randomUUID();
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
