package com.ism.views;

import com.ism.data.entities.Client;
import com.ism.data.entities.User;
import com.ism.services.IUserService;

public interface IUserView extends IView<User> {
    User accountCustomer(IUserService userService, Client client);
    int typeRole();
    User saisir(IUserService userService);
}
