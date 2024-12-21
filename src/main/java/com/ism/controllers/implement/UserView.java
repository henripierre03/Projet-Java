package com.ism.controllers.implement;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.ism.core.config.router.Router;
import com.ism.core.factory.IFactory;
import com.ism.core.factory.implement.Factory;
import com.ism.core.helper.Errors;
import com.ism.core.helper.PasswordUtils;
import com.ism.core.helper.Success;
import com.ism.data.entities.Client;
import com.ism.data.entities.User;
import com.ism.data.enums.Role;
import com.ism.services.IClientService;
import com.ism.services.IUserService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import com.ism.controllers.IUserView;

public class UserView extends ImpView<User> implements IUserView, Initializable {
    private IFactory factory = Factory.getInstance();
    private IClientService clientService;
    private IUserService userService;
    private static final String MSG_PASSWORD = "Entrez votre mot de passe : ";
    // >>Chargement des éléments: gestion de client
    @FXML
    private ComboBox<Client> comboClient;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField loginField;
    @FXML
    private TextField emailField;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnState;
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> idCol;
    @FXML
    private TableColumn<User, String> emailCol;
    @FXML
    private TableColumn<User, String> loginCol;
    @FXML
    private TableColumn<User, String> passwordCol;
    @FXML
    private TableColumn<User, Boolean> stateCol;
    // <<
    // >>Chargement des éléments: gestion de utilisateur (Admin/Boutiquier)
    @FXML
    private TextField passwordUserField;
    @FXML
    private TextField loginUserField;
    @FXML
    private TextField emailUserField;
    @FXML
    private ComboBox<Role> comboRole;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> idUserCol;
    @FXML
    private TableColumn<User, String> emailUserCol;
    @FXML
    private TableColumn<User, String> loginUserCol;
    @FXML
    private TableColumn<User, String> passwordUserCol;
    @FXML
    private TableColumn<User, String> roleUserCol;
    @FXML
    private TableColumn<User, Boolean> stateUserCol;
    // <<
    // >>Chargement des éléments: listing all users
    @FXML
    private Button btnSearch;
    @FXML
    private ComboBox<String> comboAllRole;
    @FXML
    private TableView<User> userAllTable;
    @FXML
    private TableColumn<User, Integer> idAllCol;
    @FXML
    private TableColumn<User, String> emailAllCol;
    @FXML
    private TableColumn<User, String> loginAllCol;
    @FXML
    private TableColumn<User, String> passwordAllCol;
    @FXML
    private TableColumn<User, String> roleAllCol;
    @FXML
    private TableColumn<User, Boolean> stateAllCol;
    // <<
    private Long userId;

    public UserView() {
        this.userService = factory.getFactoryService().getInstanceUserService();
        this.clientService = factory.getFactoryService().getInstanceClientService();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        this.userService = factory.getFactoryService().getInstanceUserService();
        this.clientService = factory.getFactoryService().getInstanceClientService();
        load();
        loadUsers();
        loadClient();
        loadRole();
        loadAllRole();
        loadAllUsers(userService.getAllActifs(-1, Router.userConnect));
        setBtn();
    }

    public void setBtn() {
        if (btnState == null)
            return;
        btnState.setDisable(true);
    }

    public void loadUsers() {
        if (usersTable == null)
            return;
        ObservableList<User> usersFX = FXCollections.observableArrayList();
        List<User> users = userService.findAllUsers(Router.userConnect);
        for (User u : users) {
            usersFX.add(u);
        }
        usersTable.setItems(usersFX);
        // >> Initialiser les colonnes de la table usersTable ici
        idUserCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailUserCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        loginUserCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        passwordUserCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        roleUserCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        stateUserCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        // <<
    }

    public void loadAllUsers(List<User> users) {
        if (userAllTable == null)
            return;
        ObservableList<User> usersFX = FXCollections.observableArrayList();
        for (User u : users) {
            usersFX.add(u);
        }
        userAllTable.setItems(usersFX);
        // >> Initialiser les colonnes de la table usersTable ici
        idAllCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailAllCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        loginAllCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        passwordAllCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        roleAllCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        stateAllCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        // <<
    }

    public void loadRole() {
        if (comboRole == null)
            return;
        ObservableList<Role> rolesFX = FXCollections.observableArrayList();
        rolesFX.add(Role.ADMIN);
        rolesFX.add(Role.BOUTIQUIER);
        comboRole.setItems(rolesFX);
    }

    public void loadAllRole() {
        if (comboAllRole == null)
            return;
        ObservableList<String> rolesFX = FXCollections.observableArrayList();
        rolesFX.add("ALL");
        rolesFX.add(Role.ADMIN.name());
        rolesFX.add(Role.BOUTIQUIER.name());
        rolesFX.add(Role.CLIENT.name());
        comboAllRole.setItems(rolesFX);
    }

    public void load() {
        if (userTable == null)
            return;
        ObservableList<User> usersFX = FXCollections.observableArrayList();
        List<User> users = userService.findAllClients();
        for (User u : users) {
            usersFX.add(u);
        }
        userTable.setItems(usersFX);
        // >>Initialiser les colonnes de la table userTable ici
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        stateCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        // <<
    }

    public void loadClient() {
        if (comboClient == null)
            return;
        ObservableList<Client> clientsFX = FXCollections.observableArrayList();
        List<Client> clients = clientService.findAllCustomerAvailable();
        for (Client c : clients) {
            clientsFX.add(c);
        }
        comboClient.setItems(clientsFX);
    }

    @FXML
    public void addCustomer(ActionEvent e) {
        User user = new User();
        // Récupérer le Stage actuel à partir de n'importe quel élément de la scène
        Stage stage = (Stage) loginField.getScene().getWindow();
        if (controllerInput(stage, user)) {
            user.setEmail(emailField.getText());
            user.setLogin(loginField.getText());
            user.setPassword(PasswordUtils.hashPassword(passwordField.getText()));
            user.setStatus(true);
            user.setRole(Role.CLIENT);
            user.setClient(comboClient.getValue());
            Client client = comboClient.getValue();
            client.setUser(user);
            userService.add(user);
            clientService.update(clientService.findAllCustomerAvailable(), client);
            reset(e);
            Success.showCustomPopup(stage, "Client ajouté avec succès.");
            load();
        }
    }

    @FXML
    public void addUser(ActionEvent e) {
        User user = new User();
        // Récupérer le Stage actuel à partir de n'importe quel élément de la scène
        Stage stage = (Stage) loginUserField.getScene().getWindow();
        if (controllerInputUser(stage, user)) {
            user.setEmail(emailUserField.getText());
            user.setLogin(loginUserField.getText());
            user.setPassword(PasswordUtils.hashPassword(passwordUserField.getText()));
            user.setStatus(true);
            user.setRole(comboRole.getValue());
            userService.add(user);
            resetUser(e);
            Success.showCustomPopup(stage, "Utilisateur Ajouté avec succès.");
            loadUsers();
        }
    }

    @FXML
    public void search(ActionEvent e) {
        Stage stage = (Stage) comboAllRole.getScene().getWindow();
        String input = comboAllRole.getValue();
        if (input == "ALL") {
            loadAllUsers(userService.getAllActifs(-1, Router.userConnect));
        } else if (input == "ADMIN") {
            loadAllUsers(userService.getAllActifs(0, Router.userConnect));
        } else if (input == "BOUTIQUIER") {
            loadAllUsers(userService.getAllActifs(1, Router.userConnect));
        } else if (input == "CLIENT") {
            loadAllUsers(userService.getAllActifs(2, Router.userConnect));
        }
        Success.showCustomPopup(stage, "Recherche effectué avec succès.");
    }

    private boolean controllerInput(Stage stage, User user) {
        if (comboClient.getValue() == null) {
            Errors.showCustomPopup(stage, "Erreur, veuillez sélectionner un client.");
            return false;
        } else if (emailField.getText().isEmpty()) {
            Errors.showCustomPopup(stage, "Erreur, le champ email ne doit pas être vide.");
            return false;
        } else if (loginField.getText().isEmpty()) {
            Errors.showCustomPopup(stage, "Erreur, le champ login ne doit pas être vide.");
            return false;
        } else if (passwordField.getText().isEmpty()) {
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

    private boolean controllerInputUser(Stage stage, User user) {
        if (emailUserField.getText().isEmpty()) {
            Errors.showCustomPopup(stage, "Erreur, le champ email ne doit pas être vide.");
            return false;
        } else if (loginUserField.getText().isEmpty()) {
            Errors.showCustomPopup(stage, "Erreur, le champ login ne doit pas être vide.");
            return false;
        } else if (passwordUserField.getText().isEmpty()) {
            Errors.showCustomPopup(stage, "Erreur, le champ mot de passe ne doit pas être vide.");
            return false;
        } else if (!checkEmail(emailUserField.getText())) {
            Errors.showCustomPopup(stage,
                    "Format incorrect. Veuillez entrer un email valide (par exemple : exemple@domaine.com).");
            return false;
        } else if (!checkLogin(loginUserField.getText())) {
            Errors.showCustomPopup(stage,
                    "Format incorrect. Veuillez entrer un login valide (minimum 5 caractères, sans espace, sans accent, sans caractères spéciaux).");
            return false;
        } else if (comboRole.getValue() == null) {
            Errors.showCustomPopup(stage, "Erreur, veuillez sélectionner un rôle.");
            return false;
        }
        user.setEmail(emailUserField.getText());
        if (userService.findBy(userService.findAll(), user) != null) {
            Errors.showCustomPopup(stage, "Erreur, l'email est déjà utilisé.");
            return false;
        }
        user.setLogin(loginUserField.getText());
        if (userService.findBy(userService.findAll(), user) != null) {
            Errors.showCustomPopup(stage, "Erreur, le login est déjà utilisé.");
            return false;
        }
        return true;
    }

    @FXML
    public void tableClicked(MouseEvent e) {
        btnState.setDisable(false);
        User user = userTable.getSelectionModel().getSelectedItem();
        if (user == null) 
            return;
        userId = user.getId();
        emailField.setText(user.getEmail());
        emailField.setDisable(true);
        loginField.setText(user.getLogin());
        loginField.setDisable(true);
        passwordField.clear();
        passwordField.setDisable(true);
        comboClient.setValue(user.getClient());
        comboClient.setDisable(true);
        if (user.isStatus()) {
            btnState.setText("Désactiver");
        } else {
            btnState.setText("Activer");
        }
        btnSave.setDisable(true);
    }

    @FXML
    public void tableUserClicked(MouseEvent e) {
        btnState.setDisable(false);
        User user = usersTable.getSelectionModel().getSelectedItem();
        if (user == null) 
            return;
        userId = user.getId();
        emailUserField.setText(user.getEmail());
        emailUserField.setDisable(true);
        loginUserField.setText(user.getLogin());
        loginUserField.setDisable(true);
        passwordUserField.clear();
        passwordUserField.setDisable(true);
        comboRole.setValue(user.getRole());
        comboRole.setDisable(true);
        if (user.isStatus()) {
            btnState.setText("Désactiver");
        } else {
            btnState.setText("Activer");
        }
        btnSave.setDisable(true);
    }

    @FXML
    public void changeState(ActionEvent e) {
        Stage stage = null;
        User u = new User();
        if (userId == null)
            return;
        u.setId(userId);
        User user = userService.findBy(u);
        if (user.isStatus()) {
            user.setStatus(false);
        } else {
            user.setStatus(true);
        }
        userService.update(userService.findAll(), user);
        // Récupérer le Stage actuel à partir de n'importe quel élément de la scène
        if (loginField != null) {
            stage = (Stage) loginField.getScene().getWindow();
            reset(e);
            load();
        } else if (loginUserField != null) {
            stage = (Stage) loginUserField.getScene().getWindow();
            resetUser(e);
            loadUsers();
        }
        if (user.isStatus()) {
            Success.showCustomPopup(stage, "Etat du compte activé avec succès.");
        } else {
            Success.showCustomPopup(stage, "Etat du compte désactivé avec succès.");
        }
    }

    @FXML
    public void reset(ActionEvent e) {
        emailField.clear();
        loginField.clear();
        passwordField.clear();
        comboClient.setValue(null);
        btnSave.setDisable(false);
        btnState.setDisable(true);

        emailField.setDisable(false);
        loginField.setDisable(false);
        passwordField.setDisable(false);
        comboClient.setDisable(false);
    }

    @FXML
    public void resetUser(ActionEvent e) {
        emailUserField.clear();
        loginUserField.clear();
        passwordUserField.clear();
        comboRole.setValue(null);
        btnSave.setDisable(false);
        btnState.setDisable(true);

        emailUserField.setDisable(false);
        loginUserField.setDisable(false);
        passwordUserField.setDisable(false);
        comboRole.setDisable(false);
    }

    // Finish
    @Override
    public User accountCustomer(IUserService userService, Client client) {
        User user = new User();
        System.out.println("Selection un client à créer un compte");
        // user.setEmail(checkEmail());
        // Vérification Email unique
        if (userService.findBy(userService.findAll(), user) != null) {
            System.out.println("Erreur, l'email est déjà utilisé.");
            return null;
        }
        // user.setLogin(checkLogin());
        // Vérification Login unique
        if (userService.findBy(userService.findAll(), user) != null) {
            System.out.println("Erreur, le login est déjà utilisé.");
            return null;
        }
        System.out.print(MSG_PASSWORD);
        user.setPassword(PasswordUtils.hashPassword(scanner.nextLine()));
        user.setStatus(true);
        user.setRole(Role.CLIENT);
        user.setClient(client);
        return user;
    }

    @Override
    public User saisir(IUserService userService) {
        User user = new User();
        // user.setEmail(checkEmail());
        // Vérification Email unique
        if (userService.findBy(userService.findAll(), user) != null) {
            System.out.println("Erreur, l'email est déjà utilisé.");
            return null;
        }
        // user.setLogin(checkLogin());
        // Vérification Login unique
        if (userService.findBy(userService.findAll(), user) != null) {
            System.out.println("Erreur, le login est déjà utilisé.");
            return null;
        }
        System.out.print(MSG_PASSWORD);
        user.setPassword(PasswordUtils.hashPassword(scanner.nextLine()));
        user.setRole(Role.values()[typeRole() - 1]);
        user.setStatus(true);
        return user;
    }

    @Override
    public int typeRole() {
        String choix;
        do {
            System.out.println("(1)- " + Role.values()[0].name());
            System.out.println("(2)- " + Role.values()[1].name());
            System.out.print("Choisissez un type d'utilisateur : ");
            choix = scanner.nextLine();
            if (!choix.equals("1") && !choix.equals("2")) {
                System.out.println("Erreur, choix invalide. Veuillez entrer 1 ou 2.");
            }
        } while (!choix.equals("1") && !choix.equals("2"));
        return Integer.parseInt(choix);
    }

    @Override
    public User getObject(List<User> users) {
        User user;
        String choix;
        this.afficher(users);
        do {
            user = new User();
            System.out.print("Choisissez une user par son id: ");
            choix = scanner.nextLine();
            if (isInteger(choix)) {
                user.setId(Long.parseLong(choix));
                user = userService.findBy(users, user);
            } else {
                continue;
            }
            if (user == null) {
                System.out.println("Erreur, l'user n'existe pas.");
            }
        } while (user == null);
        return user;
    }

    @Override
    public User saisir() {
        // Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saisir'");
    }
}
