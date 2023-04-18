package com.andersenlab;

import java.nio.file.Path;

public interface Settings {

    Path stateFilePath();

    String jdbcUrl();

    boolean isGarageSlotAdditionEnabled();

    boolean isGarageSlotDeletionEnabled();
}
