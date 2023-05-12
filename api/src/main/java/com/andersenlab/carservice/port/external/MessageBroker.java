package com.andersenlab.carservice.port.external;

import java.util.UUID;

public interface MessageBroker {

    void publishNewOrderId(UUID orderId);
}
