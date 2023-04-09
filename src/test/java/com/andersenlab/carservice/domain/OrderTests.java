package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.extension.CarServiceExtension;
import com.andersenlab.carservice.extension.ManualClock;
import com.andersenlab.carservice.extension.PredictableUUIDExtension;
import com.andersenlab.carservice.port.usecase.ListOrdersUseCase;
import com.andersenlab.carservice.port.usecase.OrderStatus;
import com.andersenlab.carservice.port.usecase.ViewOrderUseCase;
import com.andersenlab.carservice.port.usecase.exception.GarageSlotWasNotFound;
import com.andersenlab.carservice.port.usecase.exception.OrderWasNotFound;
import com.andersenlab.carservice.port.usecase.exception.RepairerWasNotFound;
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
            UUID orderId1,
            ManualClock clock
    ) {
        module.createOrderUseCase().create(orderId1, 100);

        assertThat(module.listOrdersUseCase().list(ListOrdersUseCase.Sort.ID))
                .containsExactly(orderView(orderId1, 100, clock.instant()));
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
            UUID garageSlot1,
            UUID orderId1,
            ManualClock clock
    ) {
        module.addGarageSlotUseCase().add(garageSlot1);
        module.createOrderUseCase().create(orderId1, 100);

        module.assignGarageSlotToOrderUseCase().assignGarageSlot(orderId1, garageSlot1);

        assertThat(module.viewOrderUseCase().view(orderId1))
                .isEqualTo(
                        new ViewOrderUseCase.OrderView(
                                orderId1,
                                100,
                                OrderStatus.IN_PROCESS,
                                Optional.of(garageSlot1),
                                Set.of(),
                                clock.instant(),
                                Optional.empty()
                        )
                );
    }

    @Test
    void givenOrderDoNotExist_whenViewOrder_thenOrderWasNotFoundThrown(
            CarServiceModule module,
            UUID orderId1
    ) {
        var useCase = module.viewOrderUseCase();

        assertThatThrownBy(() -> useCase.view(orderId1)).isInstanceOf(OrderWasNotFound.class);
    }

    @Test
    void givenGarageSlotDoNotExist_whenAssignGarageSlot_thenGarageSlotWasNotFoundThrown(
            CarServiceModule module,
            UUID garageSlot1,
            UUID orderId1
    ) {
        module.createOrderUseCase().create(orderId1, 100);
        var useCase = module.assignGarageSlotToOrderUseCase();

        assertThatThrownBy(() -> useCase.assignGarageSlot(orderId1, garageSlot1)).isInstanceOf(GarageSlotWasNotFound.class);
    }

    @Test
    void givenOrderDoNotExist_whenAssignGarageSlot_thenOrderWasNotFoundThrown(
            CarServiceModule module,
            UUID garageSlot1,
            UUID orderId1
    ) {
        module.addGarageSlotUseCase().add(garageSlot1);
        var useCase = module.assignGarageSlotToOrderUseCase();

        assertThatThrownBy(() -> useCase.assignGarageSlot(orderId1, garageSlot1)).isInstanceOf(OrderWasNotFound.class);
    }

    @Test
    void givenRepairerExists_whenAssignRepairer_thenOrderHasThatRepairerAssigned(
            CarServiceModule module,
            UUID repairer1,
            UUID orderId1,
            ManualClock clock
    ) {
        module.addRepairerUseCase().add(repairer1, "John");
        module.createOrderUseCase().create(orderId1, 100);

        module.assignRepairerToOrderUseCase().assignRepairer(orderId1, repairer1);

        assertThat(module.viewOrderUseCase().view(orderId1))
                .isEqualTo(
                        new ViewOrderUseCase.OrderView(
                                orderId1,
                                100,
                                OrderStatus.IN_PROCESS,
                                Optional.empty(),
                                Set.of(repairer1),
                                clock.instant(),
                                Optional.empty()
                        )
                );
    }

    @Test
    void givenRepairerDoNotExist_whenAssignRepairer_thenRepairerWasNotFoundThrown(
            CarServiceModule module,
            UUID repairer1,
            UUID orderId1
    ) {
        module.addRepairerUseCase().add(repairer1, "John");
        var useCase = module.assignRepairerToOrderUseCase();

        assertThatThrownBy(() -> useCase.assignRepairer(orderId1, repairer1)).isInstanceOf(OrderWasNotFound.class);
    }

    @Test
    void givenOrderDoNotExist_whenAssignRepairer_thenOrderWasNotFoundThrown(
            CarServiceModule module,
            UUID repairer1,
            UUID orderId1
    ) {
        module.createOrderUseCase().create(orderId1, 100);
        var useCase = module.assignRepairerToOrderUseCase();

        assertThatThrownBy(() -> useCase.assignRepairer(orderId1, repairer1)).isInstanceOf(RepairerWasNotFound.class);
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
