package com.ism.core.repository.implement;

import java.sql.SQLException;

import com.ism.core.database.IDatabase;
import com.ism.core.repository.IRepository;

public abstract class Repository<T> implements IRepository<T> {
    protected final IDatabase database;
    protected final Class<T> entityClass;
    protected final String tableName;

    protected Repository(IDatabase database, Class<T> entityClass, String tableName) {
        this.database = database;
        this.entityClass = entityClass;
        this.tableName = tableName;
    }

    @Override
    public String getSelectAll(String tableName) {
        return String.format("SELECT * FROM %s;", tableName);
    }

    public String getSelectBy(String tableName, Long id) {
        return String.format("SELECT * FROM %s WHERE id = %d;", tableName, id);
    }

    @Override
    public int size() {
        try {
            return selectAll().size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
