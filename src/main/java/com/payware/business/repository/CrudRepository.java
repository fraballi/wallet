package com.payware.business.repository;

import java.util.Collection;
import javax.transaction.Transactional;

public interface CrudRepository<K, T> {

    T find(Class<T> clazz, K id);

    Collection<T> findAll(Class<T> clazz);

    @Transactional
    void save(T entity);

    @Transactional
    void save(Collection<T> entities);

    @Transactional
    void update(T entity);

    @Transactional
    void delete(T entity);
}
