package com.andersenlab;

public interface Settings {

    String jdbcUrl();

    boolean isGarageSlotAdditionEnabled();

    boolean isGarageSlotDeletionEnabled();
}
