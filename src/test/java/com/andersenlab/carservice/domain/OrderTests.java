package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.extension.CarServiceExtension;
import com.andersenlab.carservice.extension.ManualClock;
import com.andersenlab.carservice.extension.PredictableUUIDExtension;
import com.andersenlab.carservice.port.usecase.ListOrdersUseCase;
import com.andersenlab.carservice.port.usecase.OrderStatus;
import com.andersenlab.carservice.port.usecase.ViewOrderUseCase;
import com.andersenlab.carservice.port.usecase.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({CarServiceExtension.class, PredictableUUIDExtension.class})
class OrderTests {

    @Test
    void givenNoOrders_whenCreateOrder_thenItShouldBeListed(
            CarServiceModule module,
            UUID orderId,
            ManualClock clock
    ) {
        module.createOrderUseCase().create(orderId, 100);

        assertThat(module.listOrdersUseCase().list(ListOrdersUseCase.Sort.ID))
                .containsExactly(orderView(orderId, 100, clock.instant()));
    }

    @Test
    void givenSomeOrders_whenListById_thenTheyShouldBeSortedById(
            CarServiceModule module,
            UUID orderId1,
            UUID orderId2,
            ManualClock clock
    ) {
        module.createOrderUseCase().create(orderId1, 100);
        module.createOrderUseCase().create(orderId2, 100);

        assertThat(module.listOrdersUseCase().list(ListOrdersUseCase.Sort.ID))
                .containsExactly(
                        orderView(orderId1, 100, clock.instant()),
                        orderView(orderId2, 100, clock.instant())
                );
    }

    @Test
    void givenSomeOrders_whenListByPrice_thenTheyShouldBeSortedByPrice(
            CarServiceModule module,
            UUID orderId1,
            UUID orderId2,
            ManualClock clock
    ) {
        module.createOrderUseCase().create(orderId1, 200);
        module.createOrderUseCase().create(orderId2, 100);

        assertThat(module.listOrdersUseCase().list(ListOrdersUseCase.Sort.PRICE))
                .containsExactly(
                        orderView(orderId2, 100, clock.instant()),
                        orderView(orderId1, 200, clock.instant())
                );
    }

    @Test
    void givenSomeOrders_whenListByCreationTimestamp_thenTheyShouldBeSortedByCreationTimestamp(
            CarServiceModule module,
            UUID orderId1,
            UUID orderId2,
            ManualClock clock
    ) {
        var timestamp = clock.instant();
        module.createOrderUseCase().create(orderId2, 100);
        clock.waitOneHour();
        module.createOrderUseCase().create(orderId1, 100);

        assertThat(module.listOrdersUseCase().list(ListOrdersUseCase.Sort.CREATION_TIMESTAMP))
                .containsExactly(
                        orderView(orderId2, 100, timestamp),
                        orderView(orderId1, 100, timestamp.plus(1, ChronoUnit.HOURS))
                );
    }

    @Test
    void givenGarageSlotExists_whenAssignGarageSlot_thenOrderHasThatGarageSlotAssigned(
            CarServiceModule module,
            UUID garageSlot,
            UUID orderId,
            ManualClock clock
    ) {
        module.addGarageSlotUseCase().add(garageSlot);
        module.createOrderUseCase().create(orderId, 100);

        module.assignGarageSlotToOrderUseCase().assignGarageSlot(orderId, garageSlot);

        assertThat(module.viewOrderUseCase().view(orderId))
                .isEqualTo(
                        new ViewOrderUseCase.OrderView(
                                orderId,
                                100,
                                OrderStatus.IN_PROCESS,
                                Optional.of(garageSlot),
                                Set.of(),
                                clock.instant(),
                                Optional.empty()
                        )
                );
    }

    @Test
    void givenOrderDoNotExist_whenViewOrder_thenOrderWasNotFoundThrown(
            CarServiceModule module,
            UUID orderId
    ) {
        var useCase = module.viewOrderUseCase();

        assertThatThrownBy(() -> useCase.view(orderId)).isInstanceOf(OrderWasNotFound.class);
    }

    @Test
    void givenGarageSlotDoNotExist_whenAssignGarageSlot_thenGarageSlotWasNotFoundThrown(
            CarServiceModule module,
            UUID garageSlot,
            UUID orderId
    ) {
        module.createOrderUseCase().create(orderId, 100);
        var useCase = module.assignGarageSlotToOrderUseCase();

        assertThatThrownBy(() -> useCase.assignGarageSlot(orderId, garageSlot)).isInstanceOf(GarageSlotWasNotFound.class);
    }

    @Test
    void givenOrderDoNotExist_whenAssignGarageSlot_thenOrderWasNotFoundThrown(
            CarServiceModule module,
            UUID garageSlot,
            UUID orderId
    ) {
        module.addGarageSlotUseCase().add(garageSlot);
        var useCase = module.assignGarageSlotToOrderUseCase();

        assertThatThrownBy(() -> useCase.assignGarageSlot(orderId, garageSlot)).isInstanceOf(OrderWasNotFound.class);
    }

    @Test
    void givenRepairerExists_whenAssignRepairer_thenOrderHasThatRepairerAssigned(
            CarServiceModule module,
            UUID repairer,
            UUID orderId,
            ManualClock clock
    ) {
        module.addRepairerUseCase().add(repairer, "John");
        module.createOrderUseCase().create(orderId, 100);

        module.assignRepairerToOrderUseCase().assignRepairer(orderId, repairer);

        assertThat(module.viewOrderUseCase().view(orderId))
                .isEqualTo(
                        new ViewOrderUseCase.OrderView(
                                orderId,
                                100,
                                OrderStatus.IN_PROCESS,
                                Optional.empty(),
                                Set.of(repairer),
                                clock.instant(),
                                Optional.empty()
                        )
                );
    }

    @Test
    void givenRepairerDoNotExist_whenAssignRepairer_thenRepairerWasNotFoundThrown(
            CarServiceModule module,
            UUID repairer,
            UUID orderId
    ) {
        module.addRepairerUseCase().add(repairer, "John");
        var useCase = module.assignRepairerToOrderUseCase();

        assertThatThrownBy(() -> useCase.assignRepairer(orderId, repairer)).isInstanceOf(OrderWasNotFound.class);
    }

    @Test
    void givenOrderDoNotExist_whenAssignRepairer_thenOrderWasNotFoundThrown(
            CarServiceModule module,
            UUID repairer,
            UUID orderId
    ) {
        module.createOrderUseCase().create(orderId, 100);
        var useCase = module.assignRepairerToOrderUseCase();

        assertThatThrownBy(() -> useCase.assignRepairer(orderId, repairer)).isInstanceOf(RepairerWasNotFound.class);
    }

    @Test
    void givenOrderHasGarageSlotAndAtLeastOneRepairerAssigned_whenCompleteOrder_thenOrderShouldBeCompleted(
            CarServiceModule module,
            UUID repairer,
            UUID garageSlot,
            UUID orderId,
            ManualClock clock
    ) {
        module.addGarageSlotUseCase().add(garageSlot);
        module.addRepairerUseCase().add(repairer, "John");
        module.createOrderUseCase().create(orderId, 100);
        module.assignGarageSlotToOrderUseCase().assignGarageSlot(orderId, garageSlot);
        module.assignRepairerToOrderUseCase().assignRepairer(orderId, repairer);

        module.completeOrderUseCase().complete(orderId);

        assertThat(module.viewOrderUseCase().view(orderId))
                .isEqualTo(
                        new ViewOrderUseCase.OrderView(
                                orderId,
                                100,
                                OrderStatus.COMPLETED,
                                Optional.of(garageSlot),
                                Set.of(repairer),
                                clock.instant(),
                                Optional.of(clock.instant())
                        )
                );
    }

    @Test
    void givenOrderHasNotGarageSlotAssigned_whenCompleteOrder_thenOrderHasNoGarageSlotAssignedThrown(
            CarServiceModule module,
            UUID repairer,
            UUID orderId
    ) {
        module.addRepairerUseCase().add(repairer, "John");
        module.createOrderUseCase().create(orderId, 100);
        module.assignRepairerToOrderUseCase().assignRepairer(orderId, repairer);
        var useCase = module.completeOrderUseCase();

        assertThatThrownBy(() -> useCase.complete(orderId)).isInstanceOf(OrderHasNoGarageSlotAssigned.class);
    }

    @Test
    void givenOrderHasNoRepairersAssigned_whenCompleteOrder_thenOrderHasNoRepairersAssignedThrown(
            CarServiceModule module,
            UUID garageSlot,
            UUID orderId
    ) {
        module.addGarageSlotUseCase().add(garageSlot);
        module.createOrderUseCase().create(orderId, 100);
        module.assignGarageSlotToOrderUseCase().assignGarageSlot(orderId, garageSlot);
        var useCase = module.completeOrderUseCase();

        assertThatThrownBy(() -> useCase.complete(orderId)).isInstanceOf(OrderHasNoRepairersAssigned.class);
    }

    @Test
    void givenOrderDoNotExist_whenCompleteOrder_thenOrderWasNotFoundThrown(CarServiceModule module, UUID orderId) {
        var useCase = module.completeOrderUseCase();

        assertThatThrownBy(() -> useCase.complete(orderId)).isInstanceOf(OrderWasNotFound.class);
    }

    @Test
    void givenOrderExists_whenCancelOrder_thenOrderShouldBeCanceled(
            CarServiceModule module,
            UUID orderId,
            ManualClock clock
    ) {
        module.createOrderUseCase().create(orderId, 100);

        module.cancelOrderUseCase().cancel(orderId);

        assertThat(module.viewOrderUseCase().view(orderId))
                .isEqualTo(
                        new ViewOrderUseCase.OrderView(
                                orderId,
                                100,
                                OrderStatus.CANCELED,
                                Optional.empty(),
                                Set.of(),
                                clock.instant(),
                                Optional.of(clock.instant())
                        )
                );
    }

    @Test
    void givenOrderDoNotExist_whenCancelOrder_thenOrderWasNotFoundThrown(CarServiceModule module, UUID orderId) {
        var useCase = module.cancelOrderUseCase();

        assertThatThrownBy(() -> useCase.cancel(orderId)).isInstanceOf(OrderWasNotFound.class);
    }

    @Test
    void givenOrderIsCompleted_whenCancelOrder_thenOrderHasBeenAlreadyCompletedThrown(
            CarServiceModule module,
            UUID repairer,
            UUID garageSlot,
            UUID orderId
    ) {
        module.addGarageSlotUseCase().add(garageSlot);
        module.addRepairerUseCase().add(repairer, "John");
        module.createOrderUseCase().create(orderId, 100);
        module.assignGarageSlotToOrderUseCase().assignGarageSlot(orderId, garageSlot);
        module.assignRepairerToOrderUseCase().assignRepairer(orderId, repairer);
        module.completeOrderUseCase().complete(orderId);
        var useCase = module.cancelOrderUseCase();

        assertThatThrownBy(() -> useCase.cancel(orderId)).isInstanceOf(OrderHasBeenAlreadyCompleted.class);
    }

    private ListOrdersUseCase.OrderView orderView(UUID id, int price, Instant timestamp) {
        return new ListOrdersUseCase.OrderView(
                id,
                price,
                OrderStatus.IN_PROCESS,
                timestamp,
                Optional.empty()
        );
    }
}
