package com.ism.core.config.router;

import com.ism.data.entities.User;

import javafx.event.ActionEvent;

public interface IRouter {
    void navigate(ActionEvent e, User user);
}
