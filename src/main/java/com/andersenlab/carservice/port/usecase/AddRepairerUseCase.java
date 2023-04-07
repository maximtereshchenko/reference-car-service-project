package com.andersenlab.carservice.port.usecase;

import java.util.UUID;

public interface AddRepairerUseCase {

    void add(UUID id, String name);
}
