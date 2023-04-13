package com.andersenlab.carservice.port.usecase;

import java.util.UUID;

public interface CreateOrderUseCase {

    void create(UUID id, long price);
}
