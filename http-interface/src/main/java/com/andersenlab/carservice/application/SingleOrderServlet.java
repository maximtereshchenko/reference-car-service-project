package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.ViewOrderUseCase;

import java.util.Map;
import java.util.UUID;

final class SingleOrderServlet extends JsonServlet {

    private final ViewOrderUseCase useCase;

    SingleOrderServlet(ViewOrderUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    Response get(String uri, Map<String, String[]> parameters) {
        return new Response(
                useCase.view(
                        UUID.fromString(
                                uri.substring(uri.lastIndexOf('/') + 1)
                        )
                )
        );
    }
}
