package com.andersenlab;

import java.nio.file.Path;

public interface Settings {

    Path stateFilePath();

    boolean isGarageSlotAdditionEnabled();

    boolean isGarageSlotDeletionEnabled();
}
