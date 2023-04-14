package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.AddGarageSlotUseCase;
import com.andersenlab.carservice.port.usecase.ListGarageSlotsUseCase;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

final class GarageSlotsServlet extends JsonServlet {

    private final ListGarageSlotsUseCase listGarageSlotsUseCase;
    private final AddGarageSlotUseCase addGarageSlotUseCase;

    GarageSlotsServlet(ListGarageSlotsUseCase listGarageSlotsUseCase, AddGarageSlotUseCase addGarageSlotUseCase) {
        this.listGarageSlotsUseCase = listGarageSlotsUseCase;
        this.addGarageSlotUseCase = addGarageSlotUseCase;
    }

    @Override
    Response get(String uri, Map<String, String[]> parameters) {
        return new Response(
                listGarageSlotsUseCase.list(
                        ListGarageSlotsUseCase.Sort.valueOf(
                                parameters.getOrDefault("sort", new String[]{""})[0]
                                        .toUpperCase(Locale.ROOT)
                        )
                )
        );
    }

    @Override
    Response post(String uri, Map<String, String> body) {
        var id = id(body);
        addGarageSlotUseCase.add(id);
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
