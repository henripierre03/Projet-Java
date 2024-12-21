package com.ism.core.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public interface IDatabase {
    void getConnection() throws SQLException;
    void closeConnection() throws SQLException;
    ResultSet executeQuery() throws SQLException;
    int executeUpdate() throws SQLException;
    void initPreparedStatement(String sql) throws SQLException;
    PreparedStatement ps();
}
