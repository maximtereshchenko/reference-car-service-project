package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.CreateOrderUseCase;
import com.andersenlab.carservice.port.usecase.ListOrdersUseCase;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

final class OrdersServlet extends JsonServlet {

    private final CreateOrderUseCase createOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;

    OrdersServlet(CreateOrderUseCase createOrderUseCase, ListOrdersUseCase listOrdersUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
    }

    @Override
    Response get(String uri, Map<String, String[]> parameters) {
        return new Response(
                listOrdersUseCase.list(
                        ListOrdersUseCase.Sort.valueOf(
                                parameters.getOrDefault("sort", new String[]{""})[0]
                                        .toUpperCase(Locale.ROOT)
                        )
                )
        );
    }

    @Override
    Response post(String uri, Map<String, String> body) {
        var id = id(body);
        createOrderUseCase.create(id, Long.parseLong(body.get("price")));
        return new Response(id);
    }

    private UUID id(Map<String, String> body) {
        var id = body.getOrDefault("id", "");
        if (id.isEmpty()) {
            return UUID.randomUUID();
        }
        return UUID.fromString(id);
    }
}
