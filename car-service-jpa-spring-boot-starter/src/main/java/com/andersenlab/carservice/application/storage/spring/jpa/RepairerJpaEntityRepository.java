package com.andersenlab.carservice.application.storage.spring.jpa;

import com.andersenlab.carservice.application.storage.jpa.RepairerJpaEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

interface RepairerJpaEntityRepository extends CrudRepository<RepairerJpaEntity, UUID> {

    List<RepairerJpaEntity> findAll(Sort sort);

    boolean existsByIdAndIsDeletedFalse(UUID id);

    boolean existsByIdAndStatusAndIsDeletedFalse(UUID id, String status);
}
