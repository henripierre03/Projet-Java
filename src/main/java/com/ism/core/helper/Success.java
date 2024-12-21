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

public class Success {
        public static void showSuccessMsg(String title, String msg) {
                Alert alert = new Alert(AlertType.INFORMATION);
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
                                        "-fx-background-color: #2ecc71;" + // Vert pour le succès
                                                        "-fx-text-fill: white;" +
                                                        "-fx-font-family: Arial;" +
                                                        "-fx-font-size: 13px;" +
                                                        "-fx-padding: 8px 20px;" +
                                                        "-fx-cursor: hand;" +
                                                        "-fx-background-radius: 5px;" +
                                                        "-fx-border-radius: 5px;");

                        // Effet hover
                        button.setOnMouseEntered(e -> button.setStyle(
                                        "-fx-background-color: #27ae60;" + // Vert plus foncé
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
                                        "-fx-background-color: #2ecc71;" +
                                                        "-fx-text-fill: white;" +
                                                        "-fx-font-family: Arial;" +
                                                        "-fx-font-size: 13px;" +
                                                        "-fx-padding: 8px 20px;" +
                                                        "-fx-cursor: hand;" +
                                                        "-fx-background-radius: 5px;" +
                                                        "-fx-border-radius: 5px;"));
                });

                // Style pour l'icône de succès
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

        public static void showTemporarySuccess(TextField field, String successMessage) {
                // Sauvegarde le style original
                String originalStyle = field.getStyle();

                // Applique le style de succès
                field.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 2px; -fx-font-family: Arial;");

                // Crée un label de succès
                Label successLabel = new Label(successMessage);
                successLabel.setTextFill(Color.web("#2ecc71")); // Vert
                successLabel.setStyle("-fx-font-family: Arial;");

                // Positionne le label sous le champ
                Pane parent = (Pane) field.getParent();
                successLabel.setLayoutX(field.getLayoutX());
                successLabel.setLayoutY(field.getLayoutY() + field.getHeight() + 5);

                parent.getChildren().add(successLabel);

                // Timeline pour effacer le message
                Timeline timeline = new Timeline(new KeyFrame(
                                Duration.seconds(3),
                                evt -> {
                                        field.setStyle(originalStyle);
                                        parent.getChildren().remove(successLabel);
                                }));
                timeline.play();
        }

        public static void showCustomPopup(Stage owner, String message) {
                Popup popup = new Popup();

                Label successLabel = new Label(message);
                VBox successBox = new VBox(successLabel);

                // Style adapté pour le succès
                successBox.setStyle(
                                "-fx-background-color: #E8F5E9;" + // Fond vert clair
                                                "-fx-padding: 10px;" +
                                                "-fx-border-color: #2ecc71;" + // Bordure verte
                                                "-fx-border-width: 1px;" +
                                                "-fx-border-radius: 5px;" +
                                                "-fx-background-radius: 5px;");

                successLabel.setStyle(
                                "-fx-text-fill: #2ecc71;" + // Texte vert
                                                "-fx-font-family: Arial;" +
                                                "-fx-font-size: 12px;");

                successLabel.setWrapText(true);
                successLabel.setMaxWidth(300);
                successBox.setMaxWidth(300);
                successBox.setAlignment(Pos.CENTER);

                popup.getContent().add(successBox);
                popup.setAutoHide(true);

                // Positionnement
                popup.show(owner);
                double centerX = owner.getX() + (owner.getWidth() - successBox.getWidth()) / 2;
                double topY = owner.getY() + 40;

                popup.setX(centerX);
                popup.setY(topY);

                Timeline timeline = new Timeline(new KeyFrame(
                                Duration.seconds(3),
                                evt -> popup.hide()));
                timeline.play();
        }

        public static void showStackPaneSuccess(StackPane container, String message) {
                // Suppression des anciens messages
                container.getChildren().removeIf(node -> node instanceof Label);

                Label successLabel = new Label(message);
                VBox successBox = new VBox(successLabel);

                // Style adapté pour le succès
                successBox.setStyle(
                                "-fx-background-color: #E8F5E9;" + // Fond vert clair
                                                "-fx-padding: 10px;" +
                                                "-fx-border-color: #2ecc71;" + // Bordure verte
                                                "-fx-border-width: 1px;" +
                                                "-fx-border-radius: 5px;" +
                                                "-fx-background-radius: 5px;");

                successLabel.setStyle(
                                "-fx-text-fill: #2ecc71;" + // Texte vert
                                                "-fx-font-family: Arial;" +
                                                "-fx-font-size: 12px;");

                successLabel.setWrapText(true);
                successLabel.setMaxWidth(300);
                successBox.setMaxWidth(300);
                successBox.setAlignment(Pos.CENTER);

                container.getChildren().add(successBox);
                StackPane.setAlignment(successBox, Pos.TOP_CENTER);
                StackPane.setMargin(successBox, new Insets(10));

                Timeline timeline = new Timeline(new KeyFrame(
                                Duration.seconds(3),
                                evt -> container.getChildren().remove(successBox)));
                timeline.play();
        }
}