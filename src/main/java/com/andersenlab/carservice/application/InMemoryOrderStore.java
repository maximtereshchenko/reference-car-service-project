package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.external.OrderStore;

import java.util.*;
import java.util.function.Function;

public final class InMemoryOrderStore implements OrderStore {

    private final Map<UUID, OrderEntity> map = new HashMap<>();
    private final Map<Sort, Comparator<OrderEntity>> comparators = Map.of(
            Sort.ID, Comparator.comparing(OrderEntity::id),
            Sort.PRICE, Comparator.comparing(OrderEntity::price),
            Sort.STATUS, Comparator.comparing(OrderEntity::status),
            Sort.CREATION_TIMESTAMP, Comparator.comparing(OrderEntity::created),
            Sort.CLOSING_TIMESTAMP, new OptionalComparator<>(OrderEntity::closed)
    );

    @Override
    public void save(OrderEntity orderEntity) {
        map.put(orderEntity.id(), orderEntity);
    }

    @Override
    public Optional<OrderEntity> findById(UUID id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public Collection<OrderProjection> findAllSorted(Sort sort) {
        return map.values()
                .stream()
                .sorted(comparators.get(sort))
                .map(orderEntity ->
                        new OrderProjection(
                                orderEntity.id(),
                                orderEntity.price(),
                                orderEntity.status(),
                                orderEntity.created(),
                                orderEntity.closed()
                        )
                )
                .toList();
    }

    private static final class OptionalComparator<T, C extends Comparable<C>> implements Comparator<T> {

        private final Function<T, Optional<C>> extractor;

        private OptionalComparator(Function<T, Optional<C>> extractor) {
            this.extractor = extractor;
        }

        @Override
        public int compare(T first, T second) {
            var firstOptional = extractor.apply(first);
            var secondOptional = extractor.apply(second);
            if (firstOptional.isEmpty() && secondOptional.isEmpty()) {
                return 0;
            }
            return firstOptional.map(firstValue ->
                            secondOptional.map(firstValue::compareTo)
                                    .orElse(1)
                    )
                    .orElse(-1);
        }
    }
}
