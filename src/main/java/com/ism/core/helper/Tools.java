package com.ism.core.helper;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Tools {
    public static void showConfirmationMsg(String title, String msg) {
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static void showErrorMsg(String title, String msg) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void loadPage(ActionEvent e, String title, String url) throws IOException {
        if (e.getSource() instanceof MenuItem) {
            // Obtenir l'item de menu qui a déclenché l'événement
            MenuItem menuItem = (MenuItem) e.getSource();
            // Obtenir la fenêtre depuis le menu
            Window window = menuItem.getParentPopup().getOwnerWindow();
            window.hide();
        } else {
            ((Node) e.getSource()).getScene().getWindow().hide();
        }
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private void loadPagePersist(ActionEvent e, String title, String url) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public static void load(ActionEvent e, String title, String url) throws IOException {
        new Tools().loadPage(e, title, url);
    }

    public static void loadPersist(ActionEvent e, String title, String url) throws IOException {
        new Tools().loadPagePersist(e, title, url);
    }
}