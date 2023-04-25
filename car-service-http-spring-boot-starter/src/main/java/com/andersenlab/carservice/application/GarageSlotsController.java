package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.AddGarageSlotUseCase;
import com.andersenlab.carservice.port.usecase.DeleteGarageSlotUseCase;
import com.andersenlab.carservice.port.usecase.ListGarageSlotsUseCase;
import com.andersenlab.carservice.port.usecase.exception.GarageSlotWithSameIdExists;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/garage-slots")
final class GarageSlotsController {

    private final ListGarageSlotsUseCase listGarageSlotsUseCase;
    private final AddGarageSlotUseCase addGarageSlotUseCase;
    private final DeleteGarageSlotUseCase deleteGarageSlotUseCase;

    GarageSlotsController(
            ListGarageSlotsUseCase listGarageSlotsUseCase,
            AddGarageSlotUseCase addGarageSlotUseCase,
            DeleteGarageSlotUseCase deleteGarageSlotUseCase
    ) {
        this.listGarageSlotsUseCase = listGarageSlotsUseCase;
        this.addGarageSlotUseCase = addGarageSlotUseCase;
        this.deleteGarageSlotUseCase = deleteGarageSlotUseCase;
    }

    @GetMapping
    List<ListGarageSlotsUseCase.GarageSlotView> list(@RequestParam String sort) {
        return listGarageSlotsUseCase.list(ListGarageSlotsUseCase.Sort.valueOf(sort.toUpperCase(Locale.ROOT)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) {
        deleteGarageSlotUseCase.delete(id);
    }

    @PostMapping
    UUID add(@RequestBody AddGarageSlotRequest request) {
        var id = request.id().orElse(UUID.randomUUID());
        try {
            addGarageSlotUseCase.add(id);
        } catch (GarageSlotWithSameIdExists e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Garage slot with same ID exists", e);
        }
        return id;
    }

    record AddGarageSlotRequest(Optional<UUID> id) {}
}
