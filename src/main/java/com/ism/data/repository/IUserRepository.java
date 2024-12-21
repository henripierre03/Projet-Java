package com.ism.data.repository;

import java.util.List;

import com.ism.core.repository.IRepository;
import com.ism.data.entities.User;

public interface IUserRepository extends IRepository<User> {
    List<User> selectAllActifs(int type);
    User selectByLogin(String login, String password);
}
