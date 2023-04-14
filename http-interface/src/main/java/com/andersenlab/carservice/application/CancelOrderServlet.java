package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.CancelOrderUseCase;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.UUID;

final class CancelOrderServlet extends JsonServlet {

    private final CancelOrderUseCase useCase;

    CancelOrderServlet(CancelOrderUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    Response post(String uri, Map<String, String> body) {
        useCase.cancel(UUID.fromString(body.get("id")));
        return new Response(HttpServletResponse.SC_OK);
    }
}
