package com.andersenlab.carservice.application.storage;

import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.external.RepairerStore;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

final class Comparators {

    private final Map<GarageSlotStore.Sort, Comparator<GarageSlotStore.GarageSlotEntity>> garageSlotComparators =
            Map.of(
                    GarageSlotStore.Sort.ID, Comparator.comparing(GarageSlotStore.GarageSlotEntity::id),
                    GarageSlotStore.Sort.STATUS, Comparator.comparing(GarageSlotStore.GarageSlotEntity::status)
            );
    private final Map<RepairerStore.Sort, Comparator<RepairerStore.RepairerEntity>> repairerEntityComparators =
            Map.of(
                    RepairerStore.Sort.ID, Comparator.comparing(RepairerStore.RepairerEntity::id),
                    RepairerStore.Sort.NAME, Comparator.comparing(RepairerStore.RepairerEntity::name),
                    RepairerStore.Sort.STATUS, Comparator.comparing(RepairerStore.RepairerEntity::status)
            );
    private final Map<OrderStore.Sort, Comparator<OrderStore.OrderEntity>> orderEntityComparators =
            Map.of(
                    OrderStore.Sort.ID, Comparator.comparing(OrderStore.OrderEntity::id),
                    OrderStore.Sort.PRICE, Comparator.comparing(OrderStore.OrderEntity::price),
                    OrderStore.Sort.STATUS, Comparator.comparing(OrderStore.OrderEntity::status),
                    OrderStore.Sort.CREATION_TIMESTAMP, Comparator.comparing(OrderStore.OrderEntity::created),
                    OrderStore.Sort.CLOSING_TIMESTAMP, new OptionalComparator<>(OrderStore.OrderEntity::closed)
            );

    private Comparators() {
        // hidden
    }

    static Comparators create() {
        var comparators = new Comparators();
        if (
                hasNotEnoughGarageEntityComparators(comparators) ||
                        hasNotEnoughRepairerEntityComparators(comparators) ||
                        hasNotEnoughOrderEntityComparators(comparators)
        ) {
            throw new IllegalArgumentException("Not enough comparators");
        }
        return comparators;
    }

    private static boolean hasNotEnoughGarageEntityComparators(Comparators comparators) {
        return comparators.garageSlotComparators.size() < GarageSlotStore.Sort.values().length;
    }

    private static boolean hasNotEnoughRepairerEntityComparators(Comparators comparators) {
        return comparators.repairerEntityComparators.size() < RepairerStore.Sort.values().length;
    }

    private static boolean hasNotEnoughOrderEntityComparators(Comparators comparators) {
        return comparators.orderEntityComparators.size() < OrderStore.Sort.values().length;
    }

    Comparator<GarageSlotStore.GarageSlotEntity> comparator(GarageSlotStore.Sort sort) {
        return garageSlotComparators.get(sort);
    }

    Comparator<RepairerStore.RepairerEntity> comparator(RepairerStore.Sort sort) {
        return repairerEntityComparators.get(sort);
    }

    Comparator<OrderStore.OrderEntity> comparator(OrderStore.Sort sort) {
        return orderEntityComparators.get(sort);
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
