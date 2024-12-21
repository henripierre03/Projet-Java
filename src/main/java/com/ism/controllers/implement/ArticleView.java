package com.ism.controllers.implement;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import com.ism.core.factory.IFactory;
import com.ism.core.factory.implement.Factory;
import com.ism.core.helper.Errors;
import com.ism.core.helper.Helper;
import com.ism.core.helper.Success;
import com.ism.data.entities.Article;
import com.ism.services.IArticleService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import com.ism.controllers.IArticleView;

public class ArticleView extends ImpView<Article> implements IArticleView, Initializable {
    private IFactory factory = Factory.getInstance();
    private IArticleService articleService;
    // >>Chargement des donnée: article
    @FXML
    private TextField libelleField;
    @FXML
    private TextField prixField;
    @FXML
    private TextField qteStockField;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnUpdate;
    @FXML
    private TableView<Article> articleTable;
    @FXML
    private TableColumn<Article, Integer> idCol;
    @FXML
    private TableColumn<Article, String> libelleCol;
    @FXML
    private TableColumn<Article, Double> prixCol;
    @FXML
    private TableColumn<Article, Integer> qteStockCol;
    @FXML
    private TableColumn<Article, LocalDateTime> dateCol;
    // <<
    private Long articleId;

    public ArticleView(IArticleService articleService) {
        this.articleService = factory.getFactoryService().getInstanceArticleService();
    }

    public ArticleView() {
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        this.articleService = factory.getFactoryService().getInstanceArticleService();
        load();
        setBtn();
    }

    public void setBtn() {
        if (btnUpdate == null)
            return;
        btnUpdate.setDisable(true);
    }

    @FXML
    public void load() {
        if (articleTable == null)
            return;
        ObservableList<Article> articleFX = FXCollections.observableArrayList();
        List<Article> users = articleService.findAll();
        for (Article u : users) {
            articleFX.add(u);
        }
        articleTable.setItems(articleFX);
        // >>Initialiser les colonnes de la table userTable ici
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        libelleCol.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
        qteStockCol.setCellValueFactory(new PropertyValueFactory<>("qteStock"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        // <<
    }

    @FXML
    public void reset(ActionEvent e) {
        libelleField.clear();
        prixField.clear();
        qteStockField.clear();
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);

        libelleField.setDisable(false);
        prixField.setDisable(false);
        qteStockField.setDisable(false);
    }

    @FXML
    public void addArticle(ActionEvent e) {
        Article article = new Article();
        // Récupérer le Stage actuel à partir de n'importe quel élément de la scène
        Stage stage = (Stage) libelleField.getScene().getWindow();
        if (controllerInput(stage, article)) {
            article.setLibelle(Helper.capitalize(libelleField.getText()));
            article.setPrix(Double.parseDouble(prixField.getText()));
            article.setQteStock(Integer.parseInt(qteStockField.getText()));
            articleService.add(article);
            reset(e);
            Success.showCustomPopup(stage, "Article ajouté avec succès.");
            load();
        }
    }

    private boolean controllerInput(Stage stage, Article article) {
        if (libelleField.getText().isEmpty()) {
            Errors.showCustomPopup(stage, "Erreur, le champ libelle ne doit pas être vide.");
            return false;
        } else if (prixField.getText().isEmpty()) {
            Errors.showCustomPopup(stage, "Erreur, le champ prix ne doit pas être vide.");
            return false;
        } else if (qteStockField.getText().isEmpty()) {
            Errors.showCustomPopup(stage, "Erreur, le champ quantité en stock ne doit pas être vide.");
            return false;
        } else if (!checkLibelle(libelleField.getText())) {
            Errors.showCustomPopup(stage, "Erreur, le libelle doit comporter entre 3 et 50 caractères.");
            return false;
        } else if (!checked(stage, prixField.getText(), "prix")) {
            return false;
        } else if (!checked(stage, qteStockField.getText(), "quantité")) {
            return false;
        }
        return true;
    }

    @FXML
    public void tableClicked(MouseEvent e) {
        btnUpdate.setDisable(false);
        Article article = articleTable.getSelectionModel().getSelectedItem();
        articleId = article.getId();
        libelleField.setText(article.getLibelle());
        libelleField.setDisable(true);
        prixField.setText(String.valueOf(article.getPrix()));
        prixField.setDisable(true);
        qteStockField.setText(String.valueOf(article.getQteStock()));
        qteStockField.setDisable(false);
        btnSave.setDisable(true);
    }

    @FXML
    public void update(ActionEvent e) {
        // Récupérer le Stage actuel à partir de n'importe quel élément de la scène
        Stage stage = (Stage) libelleField.getScene().getWindow();
        Article a = new Article();
        if (articleId == null)
            return;
        a.setId(articleId);
        Article article = articleService.findBy(a, articleService.findAll());
        if (controllerInput(stage, article)) {
            article.setQteStock(Integer.parseInt(qteStockField.getText()));
            articleService.update(article);
            reset(e);
            load();
            Success.showCustomPopup(stage, "Article modifié avec succès.");
        }
    }

    @Override
    public Article saisir() {
        Article article = new Article();
        // article.setLibelle(checkLibelle());
        // article.setPrix(Double.valueOf(checked("Entrez le prix de l'article : ", "le prix").toString()));
        // article.setQteStock(Integer.valueOf(checked("Entrez la quantité de l'article : ", "la quantité").toString()));
        return article;
    }

    private boolean checkLibelle(String libelle) {
        if (libelle.length() < 3 || libelle.length() > 50) {
            return false;
        }
        return true;
    }

    private boolean checked(Stage stage, String value, String msgError) {
        // Traitement spécifique pour la quantité
        if (msgError.contains("quantité")) {
            if (!value.matches("\\d+")) { // Vérifie si la valeur contient uniquement des chiffres
                Errors.showCustomPopup(stage, "Erreur, la " + msgError + " doit être un nombre entier.");
                return false;
            }
            int intValue = Integer.parseInt(value);
            if (intValue == 0) {
                Errors.showCustomPopup(stage, "Erreur, la valeur ne peut pas être 0.");
                return false;
            } else if (intValue < 0) {
                Errors.showCustomPopup(stage, "Erreur, la " + msgError + " ne peut être négatif.");
                return false;
            }
            return true;
        }
        
        // Traitement pour le prix
        try {
            double numValue = Double.parseDouble(value);
            if (numValue == 0) {
                Errors.showCustomPopup(stage, "Erreur, la valeur ne peut pas être 0.");
                return false;
            } else if (numValue < 0) {
                Errors.showCustomPopup(stage, "Erreur, le " + msgError + " ne peut être négatif.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            Errors.showCustomPopup(stage, "Erreur, veuillez entrer un " + msgError + " valide.");
            return false;
        }
    }

    @Override
    public Article getObject(List<Article> articles) {
        Article article;
        String choix;
        this.afficher(articles);
        do {
            article = new Article();
            System.out.print("Choisissez un article par son id: ");
            choix = scanner.nextLine();
            if (isInteger(choix)) {
                article.setId(Long.parseLong(choix));
                article = articleService.findBy(article, articles);
            } else {
                continue;
            }
            System.out.println(article);
            if (article == null) {
                System.out.println("Erreur, l'id de l'article est invalide.");
            }
        } while (article == null);
        return article;
    }
}
