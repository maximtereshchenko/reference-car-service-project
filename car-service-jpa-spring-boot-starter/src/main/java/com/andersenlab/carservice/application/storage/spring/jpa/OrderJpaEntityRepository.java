package com.andersenlab.carservice.application.storage.spring.jpa;

import com.andersenlab.carservice.application.storage.jpa.OrderJpaEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

interface OrderJpaEntityRepository extends CrudRepository<OrderJpaEntity, UUID> {

    List<OrderJpaEntity> findAll(Sort sort);
}
