package com.andersenlab.carservice.application.storage.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public final class Database {

    private final EntityManagerFactory entityManagerFactory;
    private final ThreadLocal<EntityManager> entityManagers = new ThreadLocal<>();

    public Database(String jdbcUrl) {
        entityManagerFactory = Persistence.createEntityManagerFactory(
                "default",
                Map.of(
                        "hibernate.connection.url", jdbcUrl,
                        "hibernate.hbm2ddl.auto", "validate"
                )
        );
    }

    <T> Collection<T> query(String query, Class<T> entityType) {
        if (entityManagers.get() == null) {
            throw new NoOpenEntityManager();
        }
        return entityManagers.get().createQuery(query, entityType).getResultList();
    }

    <T> void persist(T jpaEntity) {
        if (entityManagers.get() == null) {
            throw new NoOpenEntityManager();
        }
        entityManagers.get().persist(jpaEntity);
    }

    <T> Optional<T> findById(UUID id, Class<T> entityType) {
        if (entityManagers.get() == null) {
            throw new NoOpenEntityManager();
        }
        return Optional.ofNullable(entityManagers.get().find(entityType, id));
    }

    <T> T transactional(Supplier<T> action) {
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            entityManagers.set(entityManager);
            return execute(action, entityManager);
        } finally {
            entityManagers.remove();
        }
    }

    void transactional(Runnable action) {
        transactional(() -> {
            action.run();
            return null;
        });
    }

    private <T> T execute(Supplier<T> action, EntityManager entityManager) {
        try {
            var result = action.get();
            entityManager.getTransaction().commit();
            return result;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        }
    }

    private static final class NoOpenEntityManager extends RuntimeException {}
}
