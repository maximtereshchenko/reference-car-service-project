package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.extension.CarServiceExtension;
import com.andersenlab.carservice.extension.PredictableUUIDExtension;
import com.andersenlab.carservice.port.usecase.ListOrdersUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({CarServiceExtension.class, PredictableUUIDExtension.class})
class OrderTests {

    @Test
    void givenNoOrders_whenCreateOrder_thenItShouldBeListed(CarServiceModule module, UUID orderId1, Instant timestamp) {
        module.createOrderUseCase().create(orderId1, 100);

        assertThat(module.listOrdersUseCase().list(ListOrdersUseCase.Sort.ID))
                .containsExactly(
                        new ListOrdersUseCase.OrderView(
                                orderId1,
                                100,
                                ListOrdersUseCase.OrderStatus.IN_PROCESS,
                                timestamp,
                                Optional.empty()
                        )
                );
    }

    @Test
    void givenSomeOrders_whenListById_thenTheyShouldBeSortedById(
            CarServiceModule module,
            UUID orderId1,
            UUID orderId2,
            Instant timestamp
    ) {
        module.createOrderUseCase().create(orderId1, 100);
        module.createOrderUseCase().create(orderId2, 100);

        assertThat(module.listOrdersUseCase().list(ListOrdersUseCase.Sort.ID))
                .containsExactly(
                        new ListOrdersUseCase.OrderView(
                                orderId1,
                                100,
                                ListOrdersUseCase.OrderStatus.IN_PROCESS,
                                timestamp,
                                Optional.empty()
                        ),
                        new ListOrdersUseCase.OrderView(
                                orderId2,
                                100,
                                ListOrdersUseCase.OrderStatus.IN_PROCESS,
                                timestamp,
                                Optional.empty()
                        )
                );
    }
}
