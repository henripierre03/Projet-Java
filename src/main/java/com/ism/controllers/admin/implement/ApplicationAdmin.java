package com.ism.controllers.admin.implement;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.ism.data.entities.User;
import com.ism.services.IArticleService;
import com.ism.services.IClientService;
import com.ism.services.IDetteService;
import com.ism.services.IUserService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import com.ism.controllers.admin.IApplicationAdmin;
import com.ism.controllers.implement.Application;
import com.ism.core.config.router.Router;
import com.ism.core.factory.IFactory;
import com.ism.core.factory.implement.Factory;
import com.ism.core.helper.Success;
import com.ism.core.helper.Tools;

public class ApplicationAdmin extends Application implements IApplicationAdmin, Initializable {
    private IFactory factory = Factory.getInstance();
    private IArticleService articleService;
    private IClientService clientService;
    private IDetteService detteService;
    private IUserService userService;

    // FXML
    @FXML
    private Label infoConnect;
    @FXML
    private Label numAllProduct;
    @FXML
    private Label numActifUser;
    @FXML
    private Label numCustomer;
    @FXML
    private Label numSolde;

    @FXML
    private Stage stage;

    public ApplicationAdmin() {
        this.articleService = factory.getFactoryService().getInstanceArticleService();
        this.clientService = factory.getFactoryService().getInstanceClientService();
        this.detteService = factory.getFactoryService().getInstanceDetteService();
        this.userService = factory.getFactoryService().getInstanceUserService();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        this.articleService = factory.getFactoryService().getInstanceArticleService();
        this.clientService = factory.getFactoryService().getInstanceClientService();
        this.detteService = factory.getFactoryService().getInstanceDetteService();
        this.userService = factory.getFactoryService().getInstanceUserService();
        Success.showSuccessMsg("Message Connexion", "Vous êtes connecté avec succès.");
        // Ajouter des informations dans les labels
        if (infoConnect != null) {
            infoConnect.setText(Router.userParams);
        }
        // Mettre à jour les labels une fois les services initialisés
        updateLabels(); 
    }

    @FXML
    @Override
    public void loadGestionClient(ActionEvent e) throws IOException {
        Tools.loadPersist(e, "Gestion Compte Client", "/com/ism/views/gestion.admin.client.fxml");
        // Mettre à jour les labels une fois les services initialisés
        updateLabels(); 
    }

    @FXML
    @Override
    public void loadGestionUser(ActionEvent e) throws IOException {
        Tools.loadPersist(e, "Gestion Compte Utilisateur", "/com/ism/views/gestion.user.fxml");
        // Mettre à jour les labels une fois les services initialisés
        updateLabels(); 
    }

    @FXML
    @Override
    public void loadListAllUser(ActionEvent e) throws IOException {
        Tools.loadPersist(e, "Liste Compte Utilisateur", "/com/ism/views/list.users.fxml");
        // Mettre à jour les labels une fois les services initialisés
        updateLabels(); 
    }

    @FXML
    @Override
    public void loadGestionArticle(ActionEvent e) throws IOException {
        Tools.loadPersist(e, "Gestion Article", "/com/ism/views/gestion.article.fxml");
        // Mettre à jour les labels une fois les services initialisés
        updateLabels(); 
    }

    @FXML
    @Override
    public void loadArchiveDette(ActionEvent e) throws IOException {
        Tools.loadPersist(e, "Archive Dette", "/com/ism/views/archive.dette.fxml");
        // Mettre à jour les labels une fois les services initialisés
        updateLabels(); 
    }

    @Override
    @FXML
    public void logout(ActionEvent e) throws IOException {
        // Retourner à la page de connexion
        Tools.load(e, "Gestion Dette Boutiquier", "/com/ism/views/connexion.fxml");
    }

    private void updateLabels() {
        if (articleService != null && userService != null && 
            clientService != null && detteService != null) {
            numAllProduct.setText("Produits Disponibles (" + articleService.findAllAvailable().size() + ")");
            numActifUser.setText("Utilisateurs Actifs (" + userService.lengthUserActif() + ")");
            numCustomer.setText("Clients (" + clientService.length() + ")");
            numSolde.setText("Dettes Soldées (" + detteService.getAllSoldes().size() + ")");
        }
    }

    @Override
    public int menu() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'menu'");
    }

    @Override
    public void run(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
}
