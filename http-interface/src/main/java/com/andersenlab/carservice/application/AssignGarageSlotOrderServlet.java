package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.AssignGarageSlotToOrderUseCase;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.UUID;

final class AssignGarageSlotOrderServlet extends JsonServlet {

    private final AssignGarageSlotToOrderUseCase useCase;

    AssignGarageSlotOrderServlet(AssignGarageSlotToOrderUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    Response post(String uri, Map<String, String> body) {
        useCase.assignGarageSlot(UUID.fromString(body.get("id")), UUID.fromString(body.get("garageSlotId")));
        return new Response(HttpServletResponse.SC_OK);
    }
}
