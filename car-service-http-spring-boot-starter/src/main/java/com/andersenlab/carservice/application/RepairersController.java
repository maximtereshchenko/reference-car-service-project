package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.AddRepairerUseCase;
import com.andersenlab.carservice.port.usecase.DeleteRepairerUseCase;
import com.andersenlab.carservice.port.usecase.ListRepairersUseCase;
import com.andersenlab.carservice.port.usecase.exception.RepairerWithSameIdExists;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/repairers")
final class RepairersController {

    private final ListRepairersUseCase listRepairersUseCase;
    private final AddRepairerUseCase addRepairerUseCase;
    private final DeleteRepairerUseCase deleteRepairerUseCase;

    RepairersController(
            ListRepairersUseCase listRepairersUseCase,
            AddRepairerUseCase addRepairerUseCase,
            DeleteRepairerUseCase deleteRepairerUseCase
    ) {
        this.listRepairersUseCase = listRepairersUseCase;
        this.addRepairerUseCase = addRepairerUseCase;
        this.deleteRepairerUseCase = deleteRepairerUseCase;
    }

    @GetMapping
    List<ListRepairersUseCase.RepairerView> list(@RequestParam String sort) {
        return listRepairersUseCase.list(ListRepairersUseCase.Sort.valueOf(sort.toUpperCase(Locale.ROOT)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) {
        deleteRepairerUseCase.delete(id);
    }

    @PostMapping
    UUID add(@RequestBody AddRepairerRequest request) {
        var id = request.id().orElse(UUID.randomUUID());
        try {
            addRepairerUseCase.add(id, request.name());
        } catch (RepairerWithSameIdExists e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Repairer with same ID exists", e);
        }
        return id;
    }

    record AddRepairerRequest(Optional<UUID> id, String name) {}
}
