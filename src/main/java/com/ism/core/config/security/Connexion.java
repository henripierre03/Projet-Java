package com.ism.core.config.security;

import java.lang.String;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.Scanner;

import com.ism.controllers.implement.ImpView;
import com.ism.core.config.router.IRouter;
import com.ism.core.config.router.Router;
import com.ism.core.helper.Errors;
import com.ism.data.entities.User;
import com.ism.data.enums.Role;
import com.ism.data.repository.implement.UserRepository;
import com.ism.services.IUserService;
import com.ism.services.implement.UserService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Connexion implements IConnexion, Initializable {
    private IUserService userService = new UserService(new UserRepository());

    private Scanner scanner = new Scanner(System.in);

    // Récupération des données saisie
    private StackPane errorContainer;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;

    public Connexion() {
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Créer le StackPane
        errorContainer = new StackPane();

        // Récupérer le parent actuel des champs
        Pane parent = (Pane) loginField.getParent();

        // Ajouter le StackPane au début des enfants
        parent.getChildren().add(0, errorContainer);

        // Configurer la taille du StackPane
        errorContainer.setPrefWidth(parent.getWidth());
        errorContainer.setPrefHeight(50); // hauteur pour le message d'erreur
    }

    @Override
    @FXML
    public void connexion(ActionEvent e) {
        String login = loginField.getText();
        String password = passwordField.getText();
        // Récupérer le Stage actuel à partir de n'importe quel élément de la scène
        Stage stage = (Stage) loginField.getScene().getWindow();

        if (login.isEmpty() || password.isEmpty()) {
            // Afficher le popup d'erreur
            Errors.showCustomPopup(stage, "Login ou mot de passe ne doit pas être vide");
            return;
        }

       // User user = null;
       // user = userService.getByLogin(login, password);
       User user = new  User(LocalDateTime.now(),LocalDateTime.now(),"karim@gmail.com", "karim", "passer123",true ,Role.ADMIN);
        if (user == null) {
            // Choisissez la méthode qui vous convient le mieux
            Errors.showCustomPopup(stage, "Nom d'utilisateur ou mot de passe incorrect");
            return ;
        } else if (!user.isStatus()) {
            Errors.showErrorMsg("Compte Bloqué !", "Votre compte est désactivé.");
            return;
        }
        reset();
        // Retourne l'utilisateur authentifié
        switchAfterLogin(e, user);
    }

    public void reset() {
        // Réinitialiser les champs
        loginField.clear();
        passwordField.clear();
    }

    private void switchAfterLogin(ActionEvent e, User user) {
        ImpView.setScanner(this.scanner);
        IRouter router = new Router();
        router.navigate(e, user);
    }
}