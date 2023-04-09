package com.andersenlab.carservice.port.usecase;

import java.util.UUID;

public interface CompleteOrderUseCase {

    void complete(UUID id);
}
