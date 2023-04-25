package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.usecase.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
final class OrdersController {

    private final ListOrdersUseCase listOrdersUseCase;
    private final ViewOrderUseCase viewOrderUseCase;
    private final CreateOrderUseCase createOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final AssignGarageSlotToOrderUseCase assignGarageSlotToOrderUseCase;
    private final AssignRepairerToOrderUseCase assignRepairerToOrderUseCase;
    private final CompleteOrderUseCase completeOrderUseCase;

    OrdersController(
            ListOrdersUseCase listOrdersUseCase,
            ViewOrderUseCase viewOrderUseCase,
            CreateOrderUseCase createOrderUseCase,
            CancelOrderUseCase cancelOrderUseCase,
            AssignGarageSlotToOrderUseCase assignGarageSlotToOrderUseCase,
            AssignRepairerToOrderUseCase assignRepairerToOrderUseCase,
            CompleteOrderUseCase completeOrderUseCase
    ) {
        this.listOrdersUseCase = listOrdersUseCase;
        this.viewOrderUseCase = viewOrderUseCase;
        this.createOrderUseCase = createOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.assignGarageSlotToOrderUseCase = assignGarageSlotToOrderUseCase;
        this.assignRepairerToOrderUseCase = assignRepairerToOrderUseCase;
        this.completeOrderUseCase = completeOrderUseCase;
    }

    @GetMapping
    List<ListOrdersUseCase.OrderView> list(@RequestParam String sort) {
        return listOrdersUseCase.list(ListOrdersUseCase.Sort.valueOf(sort.toUpperCase(Locale.ROOT)));
    }

    @GetMapping("/{id}")
    ViewOrderUseCase.OrderView view(@PathVariable UUID id) {
        return viewOrderUseCase.view(id);
    }

    @PostMapping
    UUID create(@RequestBody CreateOrderRequest request) {
        var id = request.id().orElse(UUID.randomUUID());
        createOrderUseCase.create(id, request.price());
        return id;
    }

    @PostMapping("/{id}/cancel")
    void cancel(@PathVariable UUID id) {
        cancelOrderUseCase.cancel(id);
    }

    @PostMapping("/{id}/assign/garage-slot/{garageSlotId}")
    void assignGarageSlot(@PathVariable UUID id, @PathVariable UUID garageSlotId) {
        assignGarageSlotToOrderUseCase.assignGarageSlot(id, garageSlotId);
    }

    @PostMapping("/{id}/assign/repairer/{repairerId}")
    void assignRepairer(@PathVariable UUID id, @PathVariable UUID repairerId) {
        assignRepairerToOrderUseCase.assignRepairer(id, repairerId);
    }

    @PostMapping("/{id}/complete")
    void complete(@PathVariable UUID id) {
        completeOrderUseCase.complete(id);
    }

    record CreateOrderRequest(Optional<UUID> id, long price) {}
}
