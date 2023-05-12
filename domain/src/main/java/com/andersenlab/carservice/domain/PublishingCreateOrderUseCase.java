package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.MessageBroker;
import com.andersenlab.carservice.port.usecase.CreateOrderUseCase;

import java.util.UUID;

final class PublishingCreateOrderUseCase implements CreateOrderUseCase {

    private final CreateOrderUseCase original;
    private final MessageBroker messageBroker;

    PublishingCreateOrderUseCase(CreateOrderUseCase original, MessageBroker messageBroker) {
        this.original = original;
        this.messageBroker = messageBroker;
    }

    @Override
    public void create(UUID id, long price) {
        original.create(id, price);
        messageBroker.publishNewOrderId(id);
    }
}
