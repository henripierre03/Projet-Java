package com.ism.core.helper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.util.Duration;

public class Errors {
        public static void showErrorMsg(String title, String msg) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(msg);

                // Récupération du DialogPane
                DialogPane dialogPane = alert.getDialogPane();

                // Style moderne pour le DialogPane
                dialogPane.setStyle(
                                "-fx-background-color: white;" +
                                                "-fx-padding: 15px;" +
                                                "-fx-font-family: Arial;");

                // Style pour le contenu
                dialogPane.lookup(".content.label").setStyle(
                                "-fx-font-size: 14px;" +
                                                "-fx-font-family: Arial;" +
                                                "-fx-text-fill: #2c3e50;" +
                                                "-fx-padding: 10px 0;");

                // Style pour les boutons
                dialogPane.getButtonTypes().forEach(buttonType -> {
                        Button button = (Button) dialogPane.lookupButton(buttonType);
                        button.setStyle(
                                        "-fx-background-color: #e74c3c;" +
                                                        "-fx-text-fill: white;" +
                                                        "-fx-font-family: Arial;" +
                                                        "-fx-font-size: 13px;" +
                                                        "-fx-padding: 8px 20px;" +
                                                        "-fx-cursor: hand;" +
                                                        "-fx-background-radius: 5px;" +
                                                        "-fx-border-radius: 5px;");

                        // Effet hover
                        button.setOnMouseEntered(e -> button.setStyle(
                                        "-fx-background-color: #c0392b;" +
                                                        "-fx-text-fill: white;" +
                                                        "-fx-font-family: Arial;" +
                                                        "-fx-font-size: 13px;" +
                                                        "-fx-padding: 8px 20px;" +
                                                        "-fx-cursor: hand;" +
                                                        "-fx-background-radius: 5px;" +
                                                        "-fx-border-radius: 5px;" +
                                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);"));

                        // Retour au style normal
                        button.setOnMouseExited(e -> button.setStyle(
                                        "-fx-background-color: #e74c3c;" +
                                                        "-fx-text-fill: white;" +
                                                        "-fx-font-family: Arial;" +
                                                        "-fx-font-size: 13px;" +
                                                        "-fx-padding: 8px 20px;" +
                                                        "-fx-cursor: hand;" +
                                                        "-fx-background-radius: 5px;" +
                                                        "-fx-border-radius: 5px;"));
                });

                // Style pour l'icône d'erreur
                dialogPane.lookup(".graphic-container").setStyle(
                                "-fx-padding: 10px;" +
                                                "-fx-background-color: transparent;");

                // Ajout d'une ombre portée à la fenêtre
                dialogPane.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.2)));

                // Style pour la barre de titre
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getScene().getRoot().setStyle(
                                "-fx-background-color: white;" +
                                                "-fx-font-family: Arial;");

                // Rendre la fenêtre légèrement transparente
                stage.setOpacity(0.95);

                alert.showAndWait();
        }

        // Méthode mise à jour pour fonctionner avec n'importe quel parent
        public static void showTemporaryError(TextField field, String errorMessage) {
                // Sauvegarde le style original
                String originalStyle = field.getStyle();

                // Applique le style d'erreur
                field.setStyle("-fx-background-radius: 5px; -fx-border-radius: 5px; -fx-border-color: red; -fx-border-width: 2px; -fx-font-family: Arial;");

                // Crée un label d'erreur
                Label errorLabel = new Label(errorMessage);
                errorLabel.setTextFill(Color.RED);
                errorLabel.setStyle("-fx-font-family: Arial;");

                // Positionne le label d'erreur sous le champ
                Pane parent = (Pane) field.getParent();
                errorLabel.setLayoutX(field.getLayoutX());
                errorLabel.setLayoutY(field.getLayoutY() + field.getHeight());

                parent.getChildren().add(errorLabel);

                // Création d'une timeline pour effacer le message
                Timeline timeline = new Timeline(new KeyFrame(
                                Duration.seconds(3),
                                evt -> {
                                        field.setStyle(originalStyle);
                                        parent.getChildren().remove(errorLabel);
                                }));
                timeline.play();
        }

        // Méthode pour afficher une erreur sur une Pane (Excellent)
        public static void showCustomPopup(Stage owner, String message) {
                Popup popup = new Popup();

                // Création du label avec VBox pour la cohérence avec showStackPaneError
                Label errorLabel = new Label(message);
                VBox errorBox = new VBox(errorLabel);

                // Style identique à showStackPaneError
                errorBox.setStyle(
                                "-fx-background-color: #FFE4E1;" +
                                                "-fx-padding: 10px;" +
                                                "-fx-border-color: #FF0000;" +
                                                "-fx-border-width: 1px;" +
                                                "-fx-border-radius: 5px;" +
                                                "-fx-background-radius: 5px;");

                // Style du label identique
                errorLabel.setStyle(
                                "-fx-text-fill: #FF0000;" +
                                                "-fx-font-family: Arial;" +
                                                "-fx-font-size: 12px;");

                // Configuration similaire
                errorLabel.setWrapText(true);
                errorLabel.setMaxWidth(300);
                errorBox.setMaxWidth(300);
                errorBox.setAlignment(Pos.CENTER);

                popup.getContent().add(errorBox);
                popup.setAutoHide(true);

                // Positionnement en haut et au centre
                popup.show(owner);
                // Calcul de la position pour centrer horizontalement et placer en haut
                double centerX = owner.getX() + (owner.getWidth() - errorBox.getWidth()) / 2;
                double topY = owner.getY() + 40; // Marge depuis le haut

                // Mettre à jour la position après le show pour avoir les dimensions correctes
                popup.setX(centerX);
                popup.setY(topY);

                Timeline timeline = new Timeline(new KeyFrame(
                                Duration.seconds(3),
                                evt -> popup.hide()));
                timeline.play();
        }

        // Méthode n'affiche qu'un caractère
        public static void showStackPaneError(StackPane container, String message) {
                // Suppression des anciens messages
                container.getChildren().removeIf(node -> node instanceof Label);

                // Création du label d'erreur
                Label errorLabel = new Label(message);

                // Configuration du style dans un VBox pour meilleur contrôle
                VBox errorBox = new VBox(errorLabel);
                errorBox.setStyle(
                                "-fx-background-color: #FFE4E1;" +
                                                "-fx-padding: 10px;" +
                                                "-fx-border-color: #FF0000;" +
                                                "-fx-border-width: 1px;" +
                                                "-fx-border-radius: 5px;" +
                                                "-fx-background-radius: 5px;");

                // Style du label
                errorLabel.setStyle(
                                "-fx-text-fill: #FF0000;" +
                                                "-fx-font-family: Arial;" +
                                                "-fx-font-size: 12px;");

                // Configuration du label
                errorLabel.setWrapText(true);
                errorLabel.setMaxWidth(300); // Largeur maximale fixe

                // Configuration du VBox
                errorBox.setMaxWidth(300);
                errorBox.setAlignment(Pos.CENTER);

                // Ajout au container
                container.getChildren().add(errorBox);
                StackPane.setAlignment(errorBox, Pos.TOP_CENTER);
                StackPane.setMargin(errorBox, new Insets(10));

                // Animation de disparition
                Timeline timeline = new Timeline(new KeyFrame(
                                Duration.seconds(3),
                                evt -> container.getChildren().remove(errorBox)));
                timeline.play();
        }
}