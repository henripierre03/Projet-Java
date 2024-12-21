package com.ism.controllers.implement;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.ism.data.entities.Article;
import com.ism.data.entities.Client;
import com.ism.data.entities.DemandeArticle;
import com.ism.data.entities.DemandeDette;
import com.ism.data.entities.Detail;
import com.ism.data.entities.Dette;
import com.ism.data.entities.User;
import com.ism.data.enums.EtatDemandeDette;
import com.ism.data.enums.EtatDette;
import com.ism.services.IArticleService;
import com.ism.services.IClientService;
import com.ism.services.IDetteService;
import com.ism.services.IDemandeArticleService;
import com.ism.services.IDemandeDetteService;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.ism.controllers.IDemandeDetteView;
import com.ism.core.config.router.Router;
import com.ism.core.factory.implement.Factory;
import com.ism.core.helper.Errors;
import com.ism.core.helper.Success;

public class DemandeDetteView extends ImpView<DemandeDette> implements IDemandeDetteView, Initializable {
    private Factory factory = Factory.getInstance();
    private IArticleService articleService;
    private IClientService clientService;
    private IDetteService detteService;
    private IDemandeDetteService demandeDetteService;
    // >> FXML
    public ComboBox<String> comboState;
    public Button btnSearch;
    public TableView<DemandeDette> demandeDetteTable;
    public TableColumn<DemandeDette, Integer> idCol;
    public TableColumn<DemandeDette, String> clientCol;
    public TableColumn<DemandeDette, Double> montantTotalCol;
    public TableColumn<DemandeDette, String> articlesCol;
    public TableColumn<DemandeDette, String> etatCol;

    public TextField montantPayField;
    public Button btnSave;
    public Button btnCancel;
    public TableView<DemandeDette> askDebtTable;
    public TableView<DemandeDette> askDebtAnnulerTable;
    public TableColumn<DemandeDette, Integer> idAskCol;
    public TableColumn<DemandeDette, String> montantAskCol;
    public TableColumn<DemandeDette, String> articleAskCol;
    public TableColumn<DemandeDette, String> addressCol;
    public TableColumn<DemandeDette, String> stateAskCol;
    public ComboBox<Article> comboArticle;
    public TextField qteArticle;
    public Button btnArticle;
    public Button btnRemove;
    public Button btnRelance;
    public Button btnRefus;
    public Button btnAgree;
    public Pane rowsContainer;
    public ScrollPane scrollPane;
    public VBox articlesList;
    public TextField montantField;
    // << END FXML

    // ajoutée/supprimée
    public double cancelSize = 186;
    public double valideSize = 186;
    public double paneSize = 118;
    public int count = 0;
    public double oldPosition;
    private Long detteId;
    // <<

    public record ArticleQte(Article article, String qte) {
    }

    public DemandeDetteView() {
        this.articleService = factory.getFactoryService().getInstanceArticleService();
        this.clientService = factory.getFactoryService().getInstanceClientService();
        this.detteService = factory.getFactoryService().getInstanceDetteService();
        this.demandeDetteService = factory.getFactoryService().getInstanceDemandeDetteService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.articleService = factory.getFactoryService().getInstanceArticleService();
        this.clientService = factory.getFactoryService().getInstanceClientService();
        this.detteService = factory.getFactoryService().getInstanceDetteService();
        this.demandeDetteService = factory.getFactoryService().getInstanceDemandeDetteService();
        loadRole();
        loadAllDemandeDette(demandeDetteService.findAllByState(EtatDemandeDette.ENCOURS.name()));
        loadAllDemandeDetteAnnulerForCustomer();
        loadAllDemandeDetteForCustomer();
        initializeArticlesContainer();
        startDynamicMontantUpdate();
        loadArticles(comboArticle);
        btnPosition();
    }

    public void loadRole() {
        if (comboState == null)
            return;
        ObservableList<String> rolesFX = FXCollections.observableArrayList();
        rolesFX.add("ALL");
        rolesFX.add(EtatDemandeDette.VALIDE.name());
        rolesFX.add(EtatDemandeDette.ENCOURS.name());
        rolesFX.add(EtatDemandeDette.ANNULE.name());
        comboState.setItems(rolesFX);
    }

    @FXML
    public void updateAsk(ActionEvent e) {
        // Récupérer le Stage actuel à partir de n'importe quel élément de la scène
        Stage stage = (Stage) btnAgree.getScene().getWindow();
        DemandeDette demandeDette = new DemandeDette();
        if (detteId == null)
            return;
        demandeDette.setId(detteId);
        demandeDette = demandeDetteService.findBy(demandeDette);
        // Créé une dette
        Dette dette = new Dette();
        dette.setMontantTotal(demandeDette.getMontantTotal());
        dette.setStatus(true);
        dette.setEtat(EtatDette.ENCOURS);
        dette.setClient(demandeDette.getClient());
        dette.setDemandeDette(demandeDette);
        // Créé une détail
        for (DemandeArticle a : demandeDette.getDemandeArticles()) {
            // Update Article
            Article article = articleService.findBy(a.getArticle(), articleService.findAllAvailable());
            article.setQteStock(article.getQteStock() - a.getQteArticle());
            articleService.update(article);
            // Création détail
            Detail detail = new Detail();
            detail.setQte(a.getQteArticle());
            detail.setPrixVente(article.getPrix());
            detail.setArticle(article);
            detail.setDette(dette);
            // Ajout détail à la dette
            dette.addDetail(detail);
        }
        detteService.add(dette);
        demandeDette.setDette(dette);
        demandeDette.setEtat(EtatDemandeDette.VALIDE);
        demandeDetteService.update(demandeDetteService.findAll(), demandeDette);
        // Update dette client
        Client client = demandeDette.getClient();
        client.addDetteClient(dette);
        clientService.update(clientService.findAll(), client);
        loadAllDemandeDette(demandeDetteService.findAllByState(EtatDemandeDette.ENCOURS.name()));
        Success.showCustomPopup(stage, "Demande de dette accepter avec succès.");
    }

    @FXML
    public void refusAsk(ActionEvent e) {
        // Récupérer le Stage actuel à partir de n'importe quel élément de la scène
        Stage stage = (Stage) btnRefus.getScene().getWindow();
        DemandeDette demandeDette = new DemandeDette();
        if (detteId == null)
            return;
        demandeDette.setId(detteId);
        demandeDette = demandeDetteService.findBy(demandeDette);
        demandeDette.setEtat(EtatDemandeDette.ANNULE);
        demandeDetteService.update(demandeDetteService.findAll(), demandeDette);
        loadAllDemandeDette(demandeDetteService.findAllByState(EtatDemandeDette.ENCOURS.name()));
        Success.showCustomPopup(stage, "Demande de dette refuser avec succès.");
    }

    public void loadAllDemandeDette(List<DemandeDette> demandeDettes) {
        if (demandeDetteTable == null)
            return;
        ObservableList<DemandeDette> demandeDettesFX = FXCollections.observableArrayList();
        for (DemandeDette d : demandeDettes) {
            demandeDettesFX.add(d);
        }
        demandeDetteTable.setItems(demandeDettesFX);
        // >> Initialiser les colonnes de la table usersTable ici
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getClient() != null
                        ? cellData.getValue().getClient().getSurname()
                        : "N/A"));
        montantTotalCol.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        articlesCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getDemandeArticles().stream()
                        .map(da -> {
                            return da.getArticle().getLibelle() + " (" + da.getQteArticle() + ")";
                        })
                        .collect(Collectors.joining(", "))));
        etatCol.setCellValueFactory(new PropertyValueFactory<>("etat"));
        // <<
    }

    public void loadAllDemandeDetteForCustomer() {
        if (askDebtTable == null)
            return;
        Client client = new Client();
        client.setUser(Router.userConnect);
        List<DemandeDette> demandeDettes = demandeDetteService.findAllDemandeDettesForClient(client);
        ObservableList<DemandeDette> demandeDettesFX = FXCollections.observableArrayList();
        for (DemandeDette d : demandeDettes) {
            demandeDettesFX.add(d);
        }
        askDebtTable.setItems(demandeDettesFX);
        // >> Initialiser les colonnes de la table usersTable ici
        idAskCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        montantAskCol.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        articleAskCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getDemandeArticles().stream()
                        .map(da -> {
                            return da.getArticle().getLibelle() + " (" + da.getQteArticle() + ")";
                        })
                        .collect(Collectors.joining(", "))));
        stateAskCol.setCellValueFactory(new PropertyValueFactory<>("etat"));
        // <<
    }

    public void loadAllDemandeDetteAnnulerForCustomer() {
        if (askDebtAnnulerTable == null)
            return;
        Client client = new Client();
        client.setUser(Router.userConnect);
        List<DemandeDette> demandeDettes = demandeDetteService.findAllByState(EtatDemandeDette.ANNULE.name());
        ObservableList<DemandeDette> demandeDettesFX = FXCollections.observableArrayList();
        for (DemandeDette d : demandeDettes) {
            demandeDettesFX.add(d);
        }
        askDebtAnnulerTable.setItems(demandeDettesFX);
        // >> Initialiser les colonnes de la table usersTable ici
        idAskCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        montantAskCol.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        articleAskCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getDemandeArticles().stream()
                        .map(da -> {
                            return da.getArticle().getLibelle() + " (" + da.getQteArticle() + ")";
                        })
                        .collect(Collectors.joining(", "))));
        stateAskCol.setCellValueFactory(new PropertyValueFactory<>("etat"));
        // <<
    }

    @FXML
    public void tableClicked(MouseEvent e) {
        DemandeDette dette;
        if (askDebtAnnulerTable != null) {
            dette = askDebtAnnulerTable.getSelectionModel().getSelectedItem();
            detteId = dette.getId();
        } else if (demandeDetteTable != null) {
            dette = demandeDetteTable.getSelectionModel().getSelectedItem();
            detteId = dette.getId();
        }
    }

    @FXML
    public void update(ActionEvent e) {
        // Récupérer le Stage actuel à partir de n'importe quel élément de la scène
        Stage stage = (Stage) btnRelance.getScene().getWindow();
        DemandeDette demandeDette = new DemandeDette();
        if (detteId == null)
            return;
        demandeDette.setId(detteId);
        demandeDette = demandeDetteService.findBy(demandeDette);
        demandeDette.setEtat(EtatDemandeDette.ENCOURS);
        demandeDetteService.update(demandeDetteService.findAll(), demandeDette);
        loadAllDemandeDetteAnnulerForCustomer();
        Success.showCustomPopup(stage, "Mise à jour de la demande de dette avec succès.");
    }

    @FXML
    public void filterState(ActionEvent e) {
        Stage stage = (Stage) comboState.getScene().getWindow();
        String input = comboState.getValue();
        if (input == "ALL") {
            loadAllDemandeDette(demandeDetteService.findAllByState(input));
        } else {
            loadAllDemandeDette(demandeDetteService.findAllByState(input));
        }
        Success.showCustomPopup(stage, "Recherche effectué avec succès.");
    }

    public void btnPosition() {
        if (btnCancel == null)
            return;
        oldPosition = btnCancel.getLayoutY();
    }

    public void startDynamicMontantUpdate() {
        if (montantField == null)
            return;
        montantField.setDisable(true);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                double total = loadMontantTotal();
                montantField.setText(String.format("%.2f", total));
            });
        }, 0, 2, TimeUnit.SECONDS);
    }

    private double loadMontantTotal() {
        return getLignesArticles().stream()
                .filter(articleQte -> articleQte.article() != null && !articleQte.qte().isBlank())
                .mapToDouble(articleQte -> articleQte.article().getPrix() * Integer.parseInt(articleQte.qte()))
                .sum();
    }

    private void loadArticles(ComboBox<Article> comboBox) {
        if (comboBox == null)
            return;
        List<Article> articles = articleService.findAllAvailable();
        comboBox.setItems(FXCollections.observableArrayList(articles));

        // Configurez l'affichage des articles dans le ComboBox
        comboBox.setCellFactory(param -> new ListCell<Article>() {
            @Override
            protected void updateItem(Article item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLibelle() + " - " + item.getPrix() + " FCFA");
                }
            }
        });
    }

    private HBox createLigneArticle() {
        HBox ligne = new HBox(5);

        // Créer les composants
        ComboBox<Article> comboArticle = new ComboBox<>();
        loadArticles(comboArticle);
        comboArticle.setPrefWidth(150);
        comboArticle.setPrefHeight(30);
        comboArticle.getStyleClass().addAll("arial", "text");
        comboArticle.setOnAction(e -> startDynamicMontantUpdate());

        TextField qteField = new TextField();
        qteField.setPrefWidth(50);
        qteField.setPrefHeight(30);
        qteField.getStyleClass().add("text");
        // qteField.setOnAction(e -> checkQte(qteField, comboArticle.getValue()));
        qteField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isBlank()) {
                try {
                    Article selectedArticle = comboArticle.getValue();

                    if (selectedArticle == null) {
                        Errors.showTemporaryError(qteField, "Sélectionnez d'abord un article");
                        qteField.setText("");
                        return;
                    }

                    int qteSaisie = Integer.parseInt(newValue);
                    int qteEnStock = getQteEnStock(selectedArticle);

                    if (qteSaisie <= 0) {
                        Errors.showTemporaryError(qteField, "Quantité doit être supérieure à zéro");
                        qteField.setText("");
                        return;
                    } else if (qteSaisie > qteEnStock) {
                        Errors.showTemporaryError(qteField,
                                "Quantité insuffisante en stock. Stock disponible : " + qteEnStock);
                        // qteField.clear();
                        return;
                    } else {
                        startDynamicMontantUpdate();
                    }
                } catch (NumberFormatException ex) {
                    Errors.showTemporaryError(qteField, "Veuillez saisir un nombre valide");
                    qteField.setText("");
                }
            }
        });

        Button btnAdd = new Button("⬇");
        btnAdd.setPrefWidth(29);
        btnAdd.setPrefHeight(30);
        btnAdd.getStyleClass().add("btn-small-light");
        btnAdd.setOnAction(e -> ajouterLigne());

        Button btnSupprimer = new Button("Ⅹ");
        btnSupprimer.setPrefWidth(29);
        btnSupprimer.setPrefHeight(30);
        btnSupprimer.getStyleClass().add("btn-small-remove");
        btnSupprimer.setOnAction(e -> supprimerLigne(ligne));

        // Ajouter les composants à la ligne
        ligne.getChildren().addAll(comboArticle, qteField, btnAdd, btnSupprimer);

        return ligne;
    }

    private int getQteEnStock(Article article) {
        return articleService.findAllAvailable()
                .stream()
                .filter(a -> a.getId().equals(article.getId()))
                .findFirst()
                .map(Article::getQteStock)
                .orElse(0);
    }

    private void resizeElement(double size, boolean add) {
        // Calcul de la hauteur totale des articles
        double totalHeight = articlesList.getChildren().size() * 35; // 30 + 5 de spacing
        double maxHeight = 150; // Hauteur maximale du conteneur

        // Déterminer si nous devons ajuster les positions
        boolean shouldAdjust = totalHeight <= maxHeight;

        // Si nous avons plus d'une ligne d'articles et que nous n'avons pas atteint la
        // hauteur maximale
        if (articlesList.getChildren().size() > 1 && shouldAdjust) {
            // Calculer le décalage à appliquer
            double offset = add ? size : -size;
            if (!add) {
                count += 1;
            }
            if (count == 1) {
                offset += 20;
            } else if (count == 2) {
                offset += 15;
            } else if (count == 3) {
                offset += 10;
            }

            // Ajuster les positions des éléments en fonction du décalage
            if (btnSave != null) {
                double newPos3 = btnSave.getLayoutY() + offset;
                btnSave.setLayoutY(newPos3);
            }

            if (btnCancel != null) {
                double newPos4 = btnCancel.getLayoutY() + offset;
                btnCancel.setLayoutY(newPos4);
            }
        }
        // Si nous revenons à une seule ligne
        else if (articlesList.getChildren().size() == 1) {
            count = 0;
            // Restaurer les positions initiales
            if (btnSave != null)
                btnSave.setLayoutY(valideSize);
            if (btnCancel != null)
                btnCancel.setLayoutY(cancelSize);
        }

        // Ajuster la hauteur du scrollPane
        if (scrollPane != null) {
            double newHeight = Math.min(totalHeight, maxHeight);
            scrollPane.setPrefViewportHeight(newHeight);
        }

        oldPosition = btnCancel.getLayoutY(); // Changer la position du bouton à chaque appel
    }

    private void initializeArticlesContainer() {
        if (rowsContainer == null)
            return;
        // Créer un VBox pour contenir les lignes d'articles
        articlesList = new VBox(5); // Espacement de 5 entre les lignes
        articlesList.setPadding(new Insets(0, 0, 0, 16)); // Padding à gauche pour aligner avec les autres éléments

        // Créer le ScrollPane
        scrollPane = new ScrollPane(articlesList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(150); // Hauteur fixe pour la zone visible
        scrollPane.setLayoutY(27);
        scrollPane.setLayoutX(0);
        scrollPane.setMaxHeight(150);
        scrollPane.getStyleClass().add("custom-scroll-pane");

        // Remplacer le contenu du rowsContainer par le ScrollPane
        rowsContainer.getChildren().clear();

        // Ajouter les labels
        Label articleLabel = new Label("Article");
        articleLabel.getStyleClass().add("text");
        articleLabel.setTextFill(Color.valueOf("#f9fafc"));
        articleLabel.setLayoutX(17);
        articleLabel.setLayoutY(4);

        Label qteLabel = new Label("Qte");
        qteLabel.getStyleClass().add("text");
        qteLabel.setTextFill(Color.valueOf("#f9fafc"));
        qteLabel.setLayoutX(170);
        qteLabel.setLayoutY(4);

        rowsContainer.getChildren().addAll(articleLabel, qteLabel, scrollPane);

        // Ajouter la première ligne
        ajouterLigne();
    }

    @FXML
    public void ajouterLigne() {
        HBox nouvelleLigne = createLigneArticle();
        articlesList.getChildren().add(nouvelleLigne);

        // Ajuster la hauteur du scrollPane si nécessaire
        double totalHeight = articlesList.getChildren().size() * 35; // 30 + 5 de spacing
        double result = Math.min(totalHeight, 150);
        scrollPane.setPrefViewportHeight(result);
        // Réactiver le bouton de suppression du premier élément si nécessaire
        if (articlesList.getChildren().size() > 1) {
            HBox firstLine = (HBox) articlesList.getChildren().get(0);
            Button firstLineRemoveButton = (Button) firstLine.getChildren().get(3);
            firstLineRemoveButton.setDisable(false);
        }
        resizeElement(40, true);
    }

    private void supprimerLigne(Pane ligne) {
        int childCount = articlesList.getChildren().size();
        if (childCount <= 2) {
            // Désactiver le bouton de suppression du premier élément
            HBox firstLine = (HBox) articlesList.getChildren().get(0);
            Button firstLineRemoveButton = (Button) firstLine.getChildren().get(3);
            firstLineRemoveButton.setDisable(true);
        }
        if (childCount >= 2) {
            articlesList.getChildren().remove(ligne);

            // Réajuster la hauteur du scrollPane
            double totalHeight = articlesList.getChildren().size() * 35;
            double result = Math.min(totalHeight, 150);
            scrollPane.setPrefViewportHeight(result);
            resizeElement(40, false);

            // Réactiver le bouton de suppression du premier élément si nécessaire
            if (articlesList.getChildren().size() > 1) {
                HBox firstLine = (HBox) articlesList.getChildren().get(0);
                Button firstLineRemoveButton = (Button) firstLine.getChildren().get(3);
                firstLineRemoveButton.setDisable(false);
            }
        }
        startDynamicMontantUpdate();
    }

    private List<ArticleQte> getLignesArticles() {
        List<ArticleQte> lignes = new ArrayList<>();
        for (Node node : articlesList.getChildren()) {
            if (node instanceof HBox) {
                HBox ligne = (HBox) node;
                @SuppressWarnings("unchecked")
                ComboBox<Article> combo = (ComboBox<Article>) ligne.getChildren().get(0);
                TextField qteField = (TextField) ligne.getChildren().get(1);

                Article article = combo.getValue() != null ? combo.getValue() : null;
                String qte = !qteField.getText().isBlank() ? qteField.getText() : "";
                lignes.add(new ArticleQte(article, qte));
            }
        }

        return lignes;
    }

    @FXML
    public void resetFormDette() {
        rowsContainer.getChildren().clear();
        initializeArticlesContainer();
        montantField.setText("0.0");
        boolean isSelected = false;
        // Ajuster la position des boutons
        if (!isSelected) {
            btnSave.setLayoutY(valideSize); // Position originale
            btnCancel.setLayoutY(cancelSize);
        }
    }

    @FXML
    public void addNewAskDette(ActionEvent e) {
        Stage stage = (Stage) montantField.getScene().getWindow();
        List<ArticleQte> lignes = getLignesArticles();
        if (controllerInputQte(stage, lignes)) {
            // Initialisation de la demande de dette
            DemandeDette demandeDette = initializeDemandeDette(clientService, Router.userConnect);
            for (ArticleQte item : lignes) {
                updateArticleStock(articleService, item.article, Integer.parseInt(item.qte));
                addDemandeArticle(item.article, Integer.parseInt(item.qte()), demandeDette);
            }
            // Lier la demande de dette à son client et sauvegarder seulement si nécessaire
            Client client = clientService.findBy(clientService.findAll(), demandeDette.getClient());
            if (client != null) {
                clientService.update(clientService.findAll(), client); // Mise à jour unique du client
            }
            demandeDetteService.add(demandeDette);
            loadAllDemandeDetteForCustomer();
            resetFormDette();
            Success.showCustomPopup(stage, "Demande de dette ajoutée avec succès.");
        }
    }

    public boolean controllerInputQte(Stage stage, List<ArticleQte> lignes) {
        if (lignes.size() == 0) {
            Errors.showCustomPopup(stage, "Erreur, veuillez ajouter au moins une ligne de détail.");
            return false;
        }
        for (ArticleQte row : lignes) {
            if (!isInteger(row.qte())) {
                Errors.showCustomPopup(stage,
                        "La quantité de l'article " + (row.article() == null ? "" : row.article().getLibelle())
                                + " doit être un entier.");
                return false;
            } else if (Integer.parseInt(row.qte()) < 1) {
                Errors.showCustomPopup(stage,
                        "La quantité de l'article " + (row.article() == null ? "" : row.article().getLibelle())
                                + " doit être supérieure à 0.");
                return false;
            } else if (row.qte().isBlank() && row.article() == null) {
                Errors.showCustomPopup(stage,
                        "Erreur, les champs ne doivent pas être vide.");
                return false;
            } else if (row.qte().isBlank()) {
                Errors.showCustomPopup(stage,
                        "Erreur, la quantité de l'article " + (row.article() == null ? "" : row.article().getLibelle())
                                + " ne doit pas être vide.");
                return false;
            } else if (row.article() == null) {
                Errors.showCustomPopup(stage,
                        "Erreur, l'article " + (row.article() == null ? "" : row.article().getLibelle())
                                + " ne doit pas être vide.");
                return false;
            } else if (Integer.parseInt(row.qte) > row.article.getQteStock()) {
                Errors.showCustomPopup(stage,
                        "Quantité insuffisante en stock. Stock disponible : " + row.article.getQteStock());
                return false;
            }
        }
        return true;
    }

    @Override
    public DemandeDette saisir(IClientService clientService, IArticleService articleService,
            IDemandeArticleService demandeArticleService, User user) {
        List<Article> articleAvailable = articleService.findAllAvailable();
        if (articleAvailable.isEmpty()) {
            System.out.println("Aucun article n'a été enregistré.");
            return null;
        }

        // Initialisation de la demande de dette
        DemandeDette demandeDette = initializeDemandeDette(clientService, user);
        String choice;
        do {
            displayAvailableArticles(articleAvailable);
            choice = getUserChoice();
            if (!choice.equals("0")) {
                processArticleChoice(choice, articleAvailable, articleService, demandeDette);
            }
        } while (!choice.equals("0"));

        // Lier la demande de dette à son client et sauvegarder seulement si nécessaire
        Client client = clientService.findBy(clientService.findAll(), demandeDette.getClient());
        if (client != null) {
            clientService.update(clientService.findAll(), client); // Mise à jour unique du client
        }

        return demandeDette;
    }

    @Override
    public void afficher(List<DemandeDette> list) {
        for (DemandeDette demandeDette : list) {
            afficherDemandeDette(demandeDette);
        }
    }

    @Override
    public void afficherDemandeDette(DemandeDette demandeDette) {
        System.out.println("ID: " + demandeDette.getId());
        System.out.println("Date: " + demandeDette.getCreatedAt());
        System.out.println("Montant total: " + demandeDette.getMontantTotal() + "Franc CFA");
        System.out.println("État: " + demandeDette.getEtat());
        System.out.println(
                "Client: " + (demandeDette.getClient() != null ? demandeDette.getClient().getSurname() : "N/A"));
        System.out.println("---Articles demandés---");
        for (DemandeArticle da : demandeDette.getDemandeArticles()) {
            System.out.println("  - Article : " + da.getArticle().getLibelle());
            System.out.println("  - Quantité : " + da.getQteArticle());
            System.out.println("  - Prix de vente : " + da.getArticle().getPrix() + "Franc CFA");
            motif("-");
        }
    }

    private DemandeDette initializeDemandeDette(IClientService clientService, User user) {
        DemandeDette demandeDette = new DemandeDette();
        Client client = new Client();
        client.setUser(user);
        client = clientService.findBy(client);
        if (client == null) {
            return null;
        }
        demandeDette.setClient(client);
        demandeDette.setEtat(EtatDemandeDette.ENCOURS);
        return demandeDette;
    }

    private void displayAvailableArticles(List<Article> articleAvailable) {
        articleAvailable.forEach(System.out::println);
    }

    private String getUserChoice() {
        System.out.print("Entrez l'id de l'article de la demande de dette(0 pour terminer): ");
        return scanner.nextLine();
    }

    private void processArticleChoice(String choice, List<Article> articleAvailable, IArticleService articleService,
            DemandeDette demandeDette) {
        int quantity = getValidQuantity();
        if (quantity <= -1)
            return;

        Article article = findArticle(choice, articleAvailable, articleService);
        if (article == null)
            return;

        if (!checkStock(article, quantity))
            return;

        // updateArticleStock(article, quantity);
        addDemandeArticle(article, quantity, demandeDette);
    }

    private int getValidQuantity() {
        System.out.print("Entrez la quantité: ");
        String qte = scanner.nextLine();

        if (!qte.matches("\\d+")) {
            System.out.println("Erreur, la quantité est incorrecte.");
            return -1;
        }

        return Integer.parseInt(qte);
    }

    private Article findArticle(String id, List<Article> articleAvailable, IArticleService articleService) {
        Article article = new Article();
        if (id.matches("\\d")) {
            article.setId(Long.parseLong(id));
        }
        Article foundArticle = articleService.findBy(article, articleAvailable);

        if (foundArticle == null) {
            System.out.println("Article non trouvé.");
        }

        return foundArticle;
    }

    private boolean checkStock(Article article, int quantity) {
        if (article.getQteStock() < quantity) {
            System.out.println("Quantité insuffisante en stock.");
            return false;
        }
        return true;
    }

    private void updateArticleStock(IArticleService articleService, Article article, int quantity) {
        article.setQteStock(article.getQteStock() - quantity);
        articleService.update(article);
    }

    private void addDemandeArticle(Article article, int quantity, DemandeDette demandeDette) {
        // créer une demande d'article
        DemandeArticle demandeArticle = new DemandeArticle();
        demandeArticle.setQteArticle(quantity);
        demandeArticle.setArticle(article);

        demandeDette.setMontantTotal((article.getPrix() * quantity));
        demandeArticle.setDemandeDette(demandeDette);
        demandeDette.addDemandeArticle(demandeArticle);
    }

    @Override
    public DemandeDette getObject(List<DemandeDette> list) {
        DemandeDette demandeDette;
        String choix;
        this.afficher(list);
        do {
            demandeDette = new DemandeDette();
            System.out.print("Choisissez une demande de dette par son id: ");
            choix = scanner.nextLine();
            if (isInteger(choix)) {
                demandeDette.setId(Long.parseLong(choix));
                demandeDette = demandeDetteService.findBy(list, demandeDette);
            } else {
                continue;
            }
            if (demandeDette == null) {
                System.out.println("Erreur, l'id est invalide.");
            }

        } while (demandeDette == null);
        return demandeDette;
    }

    @Override
    public DemandeDette saisir() {
        // Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saisir'");
    }

    @Override
    public DemandeDette getObject(List<DemandeDette> list, IDemandeDetteService demandeDetteService) {
        DemandeDette demandeDette;
        String choix;
        int count = list.size();
        this.afficher(list);
        do {
            demandeDette = new DemandeDette();
            System.out.print("Choisissez une demande de dette par son id: ");
            choix = scanner.nextLine();
            if (choix.matches("\\d")) {
                demandeDette.setId(Long.parseLong(choix));
                demandeDette = demandeDetteService.findBy(list, demandeDette);
            } else {
                continue;
            }
            if (demandeDette == null || Integer.parseInt(choix) > count) {
                System.out.println("Erreur, l'id est invalide.");
            }

        } while (demandeDette == null || Integer.parseInt(choix) > count);
        return demandeDette;
    }

}
