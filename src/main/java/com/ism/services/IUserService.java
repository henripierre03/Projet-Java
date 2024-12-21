package com.ism.services;

import java.util.List;

import com.ism.data.entities.User;

public interface IUserService {
    User add(User value);
    List<User> findAll();
    User findBy(User user);
    User findBy(List<User> users, User user);
    List<User> getAllActifs(int type, User userConnect);
    User getByLogin(String login, String password);
    int length();
    void update(List<User> users, User updateUser);
    int lengthUserActif();
    List<User> getAllActifs(int type);
    List<User> findAllClients();
    List<User> findAllUsers(User userConnect);
}
