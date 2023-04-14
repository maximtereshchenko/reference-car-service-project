package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.AssignRepairerToOrderUseCase;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.UUID;

final class AssignRepairerOrderServlet extends JsonServlet {

    private final AssignRepairerToOrderUseCase useCase;

    AssignRepairerOrderServlet(AssignRepairerToOrderUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    Response post(String uri, Map<String, String> body) {
        useCase.assignRepairer(UUID.fromString(body.get("id")), UUID.fromString(body.get("repairerId")));
        return new Response(HttpServletResponse.SC_OK);
    }
}
