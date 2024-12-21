package com.ism.controllers.implement;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.ism.core.factory.IFactory;
import com.ism.core.factory.implement.Factory;
import com.ism.core.helper.Errors;
import com.ism.core.helper.Helper;
import com.ism.core.helper.PasswordUtils;
import com.ism.core.helper.Success;
import com.ism.data.entities.Article;
import com.ism.data.entities.Client;
import com.ism.data.entities.DemandeDette;
import com.ism.data.entities.Detail;
import com.ism.data.entities.Dette;
import com.ism.data.entities.Paiement;
import com.ism.data.entities.User;
import com.ism.data.enums.EtatDette;
import com.ism.data.enums.Role;
import com.ism.services.IArticleService;
import com.ism.services.IClientService;
import com.ism.services.IDetteService;
import com.ism.services.IUserService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.ism.controllers.IClientView;

public class ClientView extends ImpView<Client> implements IClientView, Initializable {
    private IFactory factory = Factory.getInstance();
    private IClientService clientService;
    private IUserService userService;
    private IArticleService articleService;
    private IDetteService detteService;
    // >> Chargement des données
    public CheckBox createAccountCheckbox;
    public CheckBox createPayCheckbox;
    public Pane accountFieldsContainer;
    public Pane payFieldsContainer;
    public TextField surnameField;
    public TextField telField;
    public TextField addressField;
    public TextField loginField;
    public TextField emailField;
    public TextField searchField;
    public TextField montantPayField;
    public PasswordField passwordField;
    public Button btnSave;
    public Button btnCancel;
    public TableView<Client> clientTable;
    public TableColumn<Client, Integer> idCol;
    public TableColumn<Client, String> surnameCol;
    public TableColumn<Client, String> telCol;
    public TableColumn<Client, String> addressCol;
    public TableColumn<Client, Double> duCol;
    public TableColumn<Client, Boolean> stateCol;
    private ObservableList<Client> masterData; // Pour stocker toutes les données
    private ObservableList<Client> filteredData; // Pour les données filtrées
    public ComboBox<Client> comboClient;
    public ComboBox<Article> comboArticle;
    public TextField qteArticle;
    public Button btnArticle;
    public Button btnRemove;
    public Pane rowsContainer;
    public ScrollPane scrollPane;
    public VBox articlesList;
    public TextField montantField;

    // Calculer la nouvelle position des éléments en fonction de la taille
    // ajoutée/supprimée
    public double checkSize = 233;
    public double valideSize = 258;
    public double cancelSize = 258;
    public double paneSize = 250;
    public int count = 0;
    public double oldPosition;
    // <<

    public record ArticleQte(Article article, String qte) {
    }

    public ClientView() {
        this.clientService = factory.getFactoryService().getInstanceClientService();
        this.userService = factory.getFactoryService().getInstanceUserService();
        this.articleService = factory.getFactoryService().getInstanceArticleService();
        this.detteService = factory.getFactoryService().getInstanceDetteService();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        this.clientService = factory.getFactoryService().getInstanceClientService();
        this.userService = factory.getFactoryService().getInstanceUserService();
        this.articleService = factory.getFactoryService().getInstanceArticleService();
        this.detteService = factory.getFactoryService().getInstanceDetteService();
        initializeArticlesContainer();
        loadAccount();
        loadClient();
        loadClientBox();
        startDynamicMontantUpdate();
        loadArticles(comboArticle);
        btnPosition();
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

    @FXML
    public void handleCreatePay(ActionEvent e) {
        boolean isSelected = createPayCheckbox.isSelected();
        payFieldsContainer.setVisible(isSelected);
        payFieldsContainer.setManaged(isSelected);
        double currentPosition = btnCancel.getLayoutY();
        // Ajuster la position des boutons
        if (isSelected) {
            btnSave.setLayoutY(currentPosition + 72); // 266 + 204 (hauteur du pane)
            btnCancel.setLayoutY(currentPosition + 72);
        } else {
            btnSave.setLayoutY(oldPosition); // Position originale
            btnCancel.setLayoutY(oldPosition);
        }
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
            if (createPayCheckbox != null) {
                double newPos1 = createPayCheckbox.getLayoutY() + offset;
                createPayCheckbox.setLayoutY(newPos1);
            }

            if (payFieldsContainer != null) {
                double newPos2 = payFieldsContainer.getLayoutY() + offset;
                payFieldsContainer.setLayoutY(newPos2);
            }

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
            if (createPayCheckbox != null)
                createPayCheckbox.setLayoutY(checkSize);
            if (payFieldsContainer != null)
                payFieldsContainer.setLayoutY(paneSize);
            if (btnSave != null)
                btnSave.setLayoutY(cancelSize);
            if (btnCancel != null)
                btnCancel.setLayoutY(valideSize);
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
        comboClient.setValue(null);
        rowsContainer.getChildren().clear();
        initializeArticlesContainer();
        montantField.setText("0.0");
        montantPayField.setText("");
        boolean isSelected = false;
        createPayCheckbox.setSelected(isSelected);
        payFieldsContainer.setVisible(isSelected);
        payFieldsContainer.setManaged(isSelected);
        // Ajuster la position des boutons
        if (!isSelected) {
            btnSave.setLayoutY(266); // Position originale
            btnCancel.setLayoutY(266);
        }
    }

    @FXML
    public void handleCreateAccount(ActionEvent event) {
        boolean isSelected = createAccountCheckbox.isSelected();
        accountFieldsContainer.setVisible(isSelected);
        accountFieldsContainer.setManaged(isSelected);
        // Ajuster la position des boutons
        if (isSelected) {
            btnSave.setLayoutY(470); // 266 + 204 (hauteur du pane)
            btnCancel.setLayoutY(470);
        } else {
            btnSave.setLayoutY(266); // Position originale
            btnCancel.setLayoutY(266);
        }
    }

    public void loadClient() {
        if (clientTable == null)
            return;
        // Initialiser les données
        masterData = FXCollections.observableArrayList(clientService.getAllActifsWithAccount());
        filteredData = FXCollections.observableArrayList(masterData);
        // Lier la TableView aux données filtrées
        clientTable.setItems(filteredData);
        // Configurer les colonnes
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        telCol.setCellValueFactory(new PropertyValueFactory<>("tel"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        duCol.setCellValueFactory(new PropertyValueFactory<>("cumulMontantDu"));
        stateCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    public void loadClientBox() {
        if (comboClient == null)
            return;
        ObservableList<Client> clientsFX = FXCollections.observableArrayList();
        List<Client> clients = clientService.getAllActifsWithAccount();
        for (Client c : clients) {
            clientsFX.add(c);
        }
        comboClient.setItems(clientsFX);
    }

    public void loadAccount() {
        if (accountFieldsContainer == null)
            return;
        // Initialiser les champs de compte comme cachés
        accountFieldsContainer.setVisible(false);
        accountFieldsContainer.setManaged(false);
        boolean isSelected = createAccountCheckbox.isSelected();
        if (isSelected) {
            createAccountCheckbox.setSelected(false);
            btnSave.setLayoutY(266); // Position originale
            btnCancel.setLayoutY(266);
        }
    }

    @FXML
    public void addNewDette(ActionEvent e) {
        Stage stage = (Stage) comboClient.getScene().getWindow();
        List<ArticleQte> lignes = getLignesArticles();
        if (controllerInputQte(stage, lignes)) {
            Dette dette = new Dette();
            Client client = comboClient.getValue();
            dette.setClient(client);
            for (ArticleQte item : lignes) {
                updateArticleStock(articleService, item.article, Integer.parseInt(item.qte));
                dette = addDemandeArticle(item.article, Integer.parseInt(item.qte()), dette);
            }
            if (createPayCheckbox.isSelected()) {
                Object[] result = getPaiementClient(dette);
                Paiement paiement = (Paiement) result[0];
                dette = (Dette) result[1];
                dette.addPaiement(paiement);
                // Mise à jour du cumul après le paiement
                client.updateCumulMontantDu();
            }
            // Transaction
            client.addDetteClient(dette);
            detteService.add(dette);
            // Mise à jour du client dans la base de données
            client.updateCumulMontantDu();
            clientService.update(clientService.findAll(), client);
            loadClient();
            resetFormDette();
            Success.showCustomPopup(stage, "Dette effectué avec succès!");
        }
    }

    private void updateArticleStock(IArticleService articleService, Article article, int quantity) {
        article.setQteStock(article.getQteStock() - quantity);
        articleService.update(article);
    }

    private Dette addDemandeArticle(Article article, int quantity, Dette dette) {
        // Ajouter une dette
        dette.setMontantTotal(dette.getMontantTotal() + (quantity * article.getPrix()));
        dette.setStatus(true);
        dette.setEtat(EtatDette.ENCOURS);
        // Ajouter un détail entre article et une dette
        Detail detail = new Detail();
        detail.setQte(quantity);
        detail.setPrixVente(article.getPrix());
        detail.setArticle(article);
        detail.setDette(dette);
        // Transaction
        dette.addDetail(detail);
        return dette;
    }

    private Object[] getPaiementClient(Dette dette) {
        Paiement paiement = new Paiement();
        paiement.setMontantPaye(Double.parseDouble(montantPayField.getText()));
        paiement.setDette(dette);
        dette.setMontantVerser(dette.getMontantVerser() + paiement.getMontantPaye());
        return new Object[] { paiement, dette };
    }

    public boolean controllerInputQte(Stage stage, List<ArticleQte> lignes) {
        if (comboClient.getValue() == null) {
            Errors.showCustomPopup(stage, "Erreur, veuillez sélectionner un client.");
            return false;
        } else if (lignes.size() == 0) {
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
        if (createPayCheckbox.isSelected()) {
            String montantPay = montantPayField.getText().replace(",", ".");
            String montantTotal = montantField.getText().replace(",", ".");
            if (montantPayField.getText().isBlank()) {
                Errors.showCustomPopup(stage, "Erreur, le champ montant ne doit pas être vide.");
                return false;
            } else if (!isInteger(montantPayField.getText())) {
                Errors.showCustomPopup(stage, "Erreur, le champ montant doit être un entier.");
                return false;
            } else if (Double.parseDouble(montantPay) > Double.parseDouble(montantTotal)) {
                Errors.showCustomPopup(stage, "Erreur, le montant payé ne peut pas être supérieur au montant total.");
                return false;
            }
        }
        return true;
    }

    @FXML
    public void addCustomer(ActionEvent e) {
        Stage stage = (Stage) surnameField.getScene().getWindow();
        Client client = new Client();
        if (controllerInputClient(stage, client)) {
            client.setSurname(Helper.capitalize(surnameField.getText().trim()));
            client.setTel("+221" + telField.getText().trim());
            client.setAddress(Helper.capitalize(addressField.getText().trim()));
            client.setStatus(true);
            if (createAccountCheckbox.isSelected()) {
                addUser(e, client);
            } else {
                clientService.add(client);
            }
            reset(e);
            loadClient();
            Success.showCustomPopup(stage, "Client ajouté avec succès.");
        }
    }

    @FXML
    public void addUser(ActionEvent e, Client client) {
        User user = new User();
        // Récupérer le Stage actuel à partir de n'importe quel élément de la scène
        Stage stage = (Stage) surnameField.getScene().getWindow();
        if (controllerInput(stage, user)) {
            user.setEmail(emailField.getText());
            user.setLogin(loginField.getText());
            user.setPassword(PasswordUtils.hashPassword(passwordField.getText()));
            user.setStatus(true);
            user.setRole(Role.CLIENT);
            client.setUser(user);
            clientService.add(client);
        }
    }

    private boolean controllerInput(Stage stage, User user) {
        if (emailField.getText().isBlank()) {
            Errors.showCustomPopup(stage, "Erreur, le champ email ne doit pas être vide.");
            return false;
        } else if (loginField.getText().isBlank()) {
            Errors.showCustomPopup(stage, "Erreur, le champ login ne doit pas être vide.");
            return false;
        } else if (passwordField.getText().isBlank()) {
            Errors.showCustomPopup(stage, "Erreur, le champ mot de passe ne doit pas être vide.");
            return false;
        } else if (!checkEmail(emailField.getText())) {
            Errors.showCustomPopup(stage, "Erreur, le format de l'email est incorrect.");
            return false;
        } else if (!checkLogin(loginField.getText())) {
            Errors.showCustomPopup(stage, "Erreur, le format du login est incorrect.");
            return false;
        }
        user.setEmail(emailField.getText());
        if (userService.findBy(userService.findAll(), user) != null) {
            Errors.showCustomPopup(stage, "Erreur, l'email est déjà utilisé.");
            return false;
        }
        user.setLogin(loginField.getText());
        if (userService.findBy(userService.findAll(), user) != null) {
            Errors.showCustomPopup(stage, "Erreur, le login est déjà utilisé.");
            return false;
        }
        return true;
    }

    private boolean controllerInputClient(Stage stage, Client client) {
        if (surnameField.getText().isBlank()) {
            Errors.showCustomPopup(stage, "Erreur, le champ surname ne doit pas être vide.");
            return false;
        } else if (telField.getText().isBlank()) {
            Errors.showCustomPopup(stage, "Erreur, le champ téléphone ne doit pas être vide.");
            return false;
        } else if (telField.getText().isBlank()) {
            Errors.showCustomPopup(stage, "Erreur, le numéro de téléphone ne peut être vide.");
            return false;
        } else if (!checkTel(telField.getText())) {
            Errors.showCustomPopup(stage,
                    "Format incorrect. Le numéro doit commencer par 70, 77 ou 78 et contenir 9 chiffres au total (par exemple : 77 xxx xx xx).");
            return false;
        }
        client.setTel("+221" + telField.getText());
        // Vérification Tel unique
        if (clientService.findBy(clientService.findAll(), client) != null) {
            Errors.showCustomPopup(stage, "Erreur, le téléphone appartient déjà à un utilisateur.");
            return false;
        }
        return true;
    }

    @FXML
    public void handleSearch(KeyEvent event) {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isBlank()) {
            filteredData.setAll(masterData);
            return;
        }
        List<Client> filteredList = masterData.stream()
                .filter(client -> (client.getTel() != null && client.getTel().toLowerCase().contains(searchText))
                        ||
                        (client.getSurname() != null && client.getSurname().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());
        filteredData.setAll(filteredList);
    }

    @FXML
    public void reset(ActionEvent e) {
        if (surnameField == null)
            return;
        surnameField.clear();
        telField.clear();
        addressField.clear();
        loginField.clear();
        emailField.clear();
        passwordField.clear();
        loadAccount();
    }

    @Override
    public Client saisir(IClientService clientService) {
        Client client = new Client();
        System.out.print("Entrez le surnom: ");
        client.setSurname(Helper.capitalize(scanner.nextLine()));
        // Vérification Surname unique
        if (clientService.findBy(clientService.findAll(), client) != null) {
            System.out.println("Erreur, le surname est déjà utilisé.");
            return null;
        }
        // client.setTel(checkTel());
        // Vérification Tel unique
        if (clientService.findBy(clientService.findAll(), client) != null) {
            System.out.println("Erreur, le téléphone appartient déjà à un utilisateur.");
            return null;
        }
        System.out.print("Entrez l'adresse: ");
        client.setAddress(Helper.capitalize(scanner.nextLine().trim()));
        client.setStatus(true);
        return client;
    }

    private Boolean checkTel(String tel) {
        if (tel.matches("(70|77|78)\\d{7}")) {
            return true;
        }
        return false;
    }

    @Override
    public Client getObject(List<Client> clients) {
        Client client;
        String choix;
        this.display(clients);
        do {
            client = new Client();
            System.out.print("Choisissez un client par son id: ");
            choix = scanner.nextLine();
            if (isInteger(choix)) {
                client.setId(Long.parseLong(choix));
                client = clientService.findBy(clients, client);
            } else {
                continue;
            }
            if (client == null) {
                System.out.println("Erreur, l'id est invalide.");
            }
        } while (client == null);
        return client;
    }

    @Override
    public void display(List<Client> clients) {
        System.out.println("Liste des clients: ");
        for (Client client : clients) {
            displayClient(client);
        }
    }

    @Override
    public void displayClient(Client client) {
        if (client == null) {
            System.out.println("Aucun client trouvé.");
            return;
        }
        motif("+");
        System.out.println("ID : " + client.getId());
        System.out.println("Surname : " + client.getSurname());
        System.out.println("Tel : " + client.getTel());
        System.out.println("Adresse : " + client.getAddress());
        System.out.println("Cumul Montant Dû : " + client.getCumulMontantDu() + "Franc CFA");
        System.out.println("Status : " + client.isStatus());
        System.out.println("User : " + (client.getUser() == null ? "N/A" : client.getUser()));
        displayDemandeDette(client);
        displayDette(client);
        motif("+");
    }

    private void displayDemandeDette(Client client) {
        List<DemandeDette> demandeDettes = client.getDemandeDettes();
        if (!demandeDettes.isEmpty()) {
            motif("-");
            System.out.println("Liste Demande de dette : ");
            for (DemandeDette dette : demandeDettes) {
                System.out.println("Montant Total: " + dette.getMontantTotal() + "Franc CFA");
                System.out.println("Date: " + dette.getCreatedAt());
                System.out.println("Etat: " + dette.getEtat());
                motif("-");
            }
        } else {
            System.out.println("Liste des demandes de dette : N/A");
        }
    }

    private void displayDette(Client client) {
        List<Dette> dettes = client.getDettes();
        if (!dettes.isEmpty()) {
            motif("-");
            System.out.println("Liste de dette : ");
            for (Dette dette : dettes) {
                System.out.println("Montant Total: " + dette.getMontantTotal() + "Franc CFA");
                System.out.println("Montant Verser: " + dette.getMontantVerser() + "Franc CFA");
                System.out.println("Montant Restant: " + dette.getMontantRestant() + "Franc CFA");
                System.out.println("Status: " + dette.isStatus());
                System.out.println("État: " + dette.getEtat());
                System.out.println("Date: " + dette.getCreatedAt());
            }
        } else {
            System.out.println("Liste des dettes : N/A");
        }
    }

    @Override
    public Client saisir() {
        // Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saisir'");
    }
}
