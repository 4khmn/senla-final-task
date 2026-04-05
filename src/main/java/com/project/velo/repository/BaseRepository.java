package com.project.velo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository <T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> entityClass;

    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    public List<T> findAll() {
        CriteriaQuery<T> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(entityClass);
        criteriaQuery.from(entityClass);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public void delete(T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    public void saveAndFlush(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
    }

}
