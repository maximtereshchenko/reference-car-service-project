package com.andersenlab;

import java.util.UUID;

public final class StaticSettings implements Settings {

    @Override
    public String jdbcUrl() {
        return "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false".formatted(UUID.randomUUID());
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
