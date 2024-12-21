package com.ism.core.repository;

import java.util.List;

public interface IRepository<T> {
    T selectBy(Long id);
    void update(T entity);
    T insert(T entity);
    List<T> selectAll();
    int size();
}
