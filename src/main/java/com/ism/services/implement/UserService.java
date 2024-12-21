package com.ism.services.implement;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
    public User add(User value) {
        return userRepository.insert(value);
    }

    @Override
    public List<User> findAll() {
        return userRepository.selectAll();
    }

    @Override
    public List<User> findAllClients() {
        return userRepository.selectAllClients();
    }

    @Override
    public List<User> findAllUsers(User userConnect) {
        return userRepository.selectAllUsers(userConnect);
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
            if (us.getId() == user.getId()) {
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
    public List<User> getAllActifs(int type) {
        return userRepository.selectAllActifs(type)
                .stream()
                .filter(us -> us.getClient() != null)
                .collect(Collectors.toList());
    }

    @Override
    public int lengthUserActif() {
        return userRepository.selectAllActifs(0).size() + userRepository.selectAllActifs(1).size() + userRepository.selectAllActifs(2).size();
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
        updateUser.setUpdatedAt(LocalDateTime.now());
        userRepository.update(updateUser);
    }
}
