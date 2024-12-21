package com.ism.services.implement;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.sql.SQLException;
import java.util.Collections;
import java.time.LocalDateTime;

import com.ism.data.entities.User;
import com.ism.data.repository.IUserRepository;
import com.ism.services.IUserService;

public class UserService implements IUserService {
    private IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean add(User value) {
        try {
            return userRepository.insert(value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<User> findAll() {
        try {
            return userRepository.selectAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public User findBy(User user) {
        for (User us : findAll()) {
            if (Objects.equals(us.getId(), user.getId())) {
                return us;
            }
        }
        return null;
    }

    @Override
    public User findBy(List<User> users, User user) {
        for (User us : users) {
            if (Objects.equals(us.getId(), user.getId())) {
                return us;
            }
            if (user.getLogin() != null && us.getLogin().compareTo(user.getLogin()) == 0) {
                return us;
            }
            if (user.getEmail() != null && us.getEmail().compareTo(user.getEmail()) == 0) {
                return us;
            }
        }
        return null;
    }

    @Override
    public List<User> getAllActifs(int type, User userConnect) {
        return userRepository.selectAllActifs(type)
                .stream()
                .filter(us -> !Objects.equals(us.getId(), userConnect.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public User getByLogin(String login, String password) {
        return userRepository.selectByLogin(login, password);
    }

    @Override
    public int length() {
        return userRepository.size();
    }

    @Override
    public void update(List<User> users, User updateUser) {
        try {
            updateUser.setUpdatedAt(LocalDateTime.now());
            userRepository.update(updateUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
