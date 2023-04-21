package com.andersenlab.carservice.application.storage.jpa;

import com.andersenlab.carservice.port.external.OrderStore;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "orders")
class OrderJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "price", nullable = false)
    private long price;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "garage_slot_id")
    private UUID garageSlotId;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "repairers_in_orders", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "repairer_id")
    private Set<UUID> repairers;
    @Column(name = "created", nullable = false)
    private Instant created;
    @Column(name = "closed")
    private Instant closed;

    protected OrderJpaEntity() {
    }

    OrderJpaEntity(OrderStore.OrderEntity orderEntity) {
        id = orderEntity.id();
        update(orderEntity);
    }

    void update(OrderStore.OrderEntity orderEntity) {
        price = orderEntity.price();
        status = orderEntity.status().name();
        garageSlotId = orderEntity.garageSlotId().orElse(null);
        repairers = Set.copyOf(orderEntity.repairers());
        created = orderEntity.created();
        closed = orderEntity.closed().orElse(null);
    }

    OrderStore.OrderEntity orderEntity() {
        return new OrderStore.OrderEntity(
                id,
                price,
                OrderStore.OrderStatus.valueOf(status),
                Optional.ofNullable(garageSlotId),
                Set.copyOf(repairers),
                created,
                Optional.ofNullable(closed)
        );
    }

    OrderStore.OrderProjection orderProjection() {
        return new OrderStore.OrderProjection(
                id,
                price,
                OrderStore.OrderStatus.valueOf(status),
                created,
                Optional.ofNullable(closed)
        );
    }
}
