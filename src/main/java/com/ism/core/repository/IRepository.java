package com.ism.core.repository;

import java.sql.SQLException;
import java.util.List;

public interface IRepository<T> {
    T selectBy(Long id) throws SQLException;
    void update(T entity) throws SQLException;
    boolean insert(T entity) throws SQLException;
    List<T> selectAll() throws SQLException;
    String getSelectAll(String tableName);
    String generateUpdateSQL();
    String generateInsertSQL();
    int size();
}
