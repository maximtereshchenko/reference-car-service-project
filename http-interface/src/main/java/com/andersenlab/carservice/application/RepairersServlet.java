package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.AddRepairerUseCase;
import com.andersenlab.carservice.port.usecase.ListRepairersUseCase;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

final class RepairersServlet extends JsonServlet {

    private final ListRepairersUseCase listRepairersUseCase;
    private final AddRepairerUseCase addRepairerUseCase;

    RepairersServlet(ListRepairersUseCase listRepairersUseCase, AddRepairerUseCase addRepairerUseCase) {
        this.listRepairersUseCase = listRepairersUseCase;
        this.addRepairerUseCase = addRepairerUseCase;
    }

    @Override
    Response get(String uri, Map<String, String[]> parameters) {
        return new Response(
                listRepairersUseCase.list(
                        ListRepairersUseCase.Sort.valueOf(
                                parameters.getOrDefault("sort", new String[]{""})[0]
                                        .toUpperCase(Locale.ROOT)
                        )
                )
        );
    }

    @Override
    Response post(String uri, Map<String, String> body) {
        var id = id(body);
        addRepairerUseCase.add(id, body.get("name"));
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
