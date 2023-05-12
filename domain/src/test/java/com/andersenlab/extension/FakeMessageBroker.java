package com.andersenlab.extension;

import com.andersenlab.carservice.port.external.MessageBroker;

import java.util.UUID;

public final class FakeMessageBroker implements MessageBroker {

    private UUID published;

    @Override
    public void publishNewOrderId(UUID orderId) {
        published = orderId;
    }

    public UUID published() {
        return published;
    }
}
