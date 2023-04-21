package com.andersenlab.carservice.application.storage.jpa;

import java.util.UUID;

final class CouldNotFindEntity extends RuntimeException {

    CouldNotFindEntity(UUID id) {
        super("Could not find entity with ID " + id);
    }
}
