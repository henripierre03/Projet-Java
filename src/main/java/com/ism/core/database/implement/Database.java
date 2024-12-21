package com.ism.core.database.implement;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;


import com.ism.core.database.IDatabase;

public class Database implements IDatabase {
    private static final String URL = "jdbc:postgresql://localhost:5444/gestion_dette_jdbc";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";
    protected PreparedStatement ps;
    protected Connection conn = null;

    @Override
    public void getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver non trouvé", e);
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Override
    public int executeUpdate() throws SQLException {
        return ps.executeUpdate();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return ps.executeQuery();
    }

    @Override
    public void initPreparedStatement(String sql) throws SQLException {
        if (sql.toUpperCase().trim().startsWith("INSERT")) {
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        } else {
            ps = conn.prepareStatement(sql);
        }
    }

    @Override
    public PreparedStatement ps() {
        return ps;
    }
}
