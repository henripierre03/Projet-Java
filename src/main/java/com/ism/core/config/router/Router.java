package com.ism.core.config.router;

import java.io.IOException;

import com.ism.core.helper.Tools;
import com.ism.data.entities.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class Router implements IRouter {

    public static User userConnect;
    public static String userParams;

    public Router() {
    }

    @FXML
    @Override
    public void navigate(ActionEvent e, User user) {
        userConnect = user;
        switch (user.getRole().name()) {
            case "ADMIN":
                try {
                    userParams = "Vous êtes connecté en tant que Admin (" + user.getLogin() + "🔴)";
                    Tools.load(e, "Gestion de Dette", "/com/ism/views/dashboard.admin.fxml");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            case "CLIENT":
                try {
                    userParams = "Vous êtes connecté en tant que Client (" + user.getLogin() + "🔴)";
                    Tools.load(e, "Gestion de Dette", "/com/ism/views/dashboard.client.fxml");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            case "BOUTIQUIER":
                try {
                    userParams = "Vous êtes connecté en tant que Boutiquier (" + user.getLogin() + "🔴)";
                    Tools.load(e, "Gestion de Dette", "/com/ism/views/dashboard.storekeeper.fxml");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
