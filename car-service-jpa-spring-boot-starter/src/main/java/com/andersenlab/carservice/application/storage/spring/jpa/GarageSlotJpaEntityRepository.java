package com.andersenlab.carservice.application.storage.spring.jpa;

import com.andersenlab.carservice.application.storage.jpa.GarageSlotJpaEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

interface GarageSlotJpaEntityRepository extends CrudRepository<GarageSlotJpaEntity, UUID> {

    List<GarageSlotJpaEntity> findAll(Sort sort);

    boolean existsByIdAndIsDeletedFalse(UUID id);

    boolean existsByIdAndStatusAndIsDeletedFalse(UUID id, String status);
}
