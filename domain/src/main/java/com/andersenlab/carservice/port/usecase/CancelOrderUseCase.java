package com.andersenlab.carservice.port.usecase;

import java.util.UUID;

public interface CancelOrderUseCase {

    void cancel(UUID id);
}
