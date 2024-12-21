package com.ism.data.repository.implement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.ism.core.database.IDatabase;
import com.ism.core.helper.PasswordUtils;
import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.User;
import com.ism.data.enums.Role;
import com.ism.data.repository.IUserRepository;

public class UserRepository extends Repository<User> implements IUserRepository {
    public UserRepository(IDatabase database) {
        super(database, User.class, "users");
    }

    @Override
    public String generateInsertSQL() {
        return """
                    INSERT INTO users (
                        email, login, password, status, role, created_at, updated_at
                    ) VALUES (
                        ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                    )
                """;
    }

    @Override
    public String generateUpdateSQL() {
        return """
                    UPDATE users SET 
                        email=?, 
                        login=?, 
                        password=?, 
                        status=?, 
                        role=?, 
                        updated_at=? 
                    WHERE id=?
                """;
    }

    @Override
    public List<User> selectAllActifs(int type) {
        try {
            return selectAll().stream()
                    .filter(user -> user.isStatus() && user.getRole().ordinal() == type)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public User selectByLogin(String login, String password) {
        try {
            return selectAll().stream()
                    .filter(user -> user.getLogin().compareTo(login) == 0
                            && PasswordUtils.checkPassword(password, user.getPassword()))
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User selectBy(Long id) throws SQLException {
        User user = null;
        String sql = getSelectBy(tableName, id);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            if (rs.next()) {
                user = this.convertToObject(rs);
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return user;
    }

    @Override
    public void update(User entity) throws SQLException {
        String sql = generateUpdateSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            // Setting parameters for the update statement
            database.ps().setString(1, entity.getEmail());
            database.ps().setString(2, entity.getLogin());
            database.ps().setString(3, entity.getPassword());
            database.ps().setBoolean(4, entity.isStatus());
            database.ps().setString(5, entity.getRole().name());
            database.ps().setTimestamp(6, Timestamp.valueOf(entity.getUpdatedAt()));
            database.ps().setLong(7, entity.getId());

            database.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            database.closeConnection();
        }
    }

    @Override
    public boolean insert(User entity) throws SQLException {
        String sql = generateInsertSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            // Setting parameters for the update statement
            database.ps().setString(1, entity.getEmail());
            database.ps().setString(2, entity.getLogin());
            database.ps().setString(3, entity.getPassword());
            database.ps().setBoolean(4, entity.isStatus());
            database.ps().setString(5, entity.getRole().name());

            database.executeUpdate();

            ResultSet rs = database.ps().getGeneratedKeys();
            if (rs.next()) {
                entity.setId(rs.getLong(1));
            }
            rs.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<User> selectAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = getSelectAll(tableName);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            while (rs.next()) {
                users.add(this.convertToObject(rs));
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return users;
    }

    private User convertToObject(ResultSet rs) {
        try {
            return new User(
                    rs.getLong("id"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime(),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("password"),
                    rs.getBoolean("status"),
                    Role.valueOf(rs.getString("role"))
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
