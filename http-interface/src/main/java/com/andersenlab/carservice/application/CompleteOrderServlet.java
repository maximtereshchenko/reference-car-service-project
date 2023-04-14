package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.CompleteOrderUseCase;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.UUID;

final class CompleteOrderServlet extends JsonServlet {

    private final CompleteOrderUseCase useCase;

    CompleteOrderServlet(CompleteOrderUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    Response post(String uri, Map<String, String> body) {
        useCase.complete(UUID.fromString(body.get("id")));
        return new Response(HttpServletResponse.SC_OK);
    }
}
