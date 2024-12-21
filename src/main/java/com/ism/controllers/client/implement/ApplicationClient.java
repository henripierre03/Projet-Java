package com.ism.controllers.client.implement;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.ism.data.entities.Client;
import com.ism.data.entities.DemandeDette;
import com.ism.data.entities.Detail;
import com.ism.data.entities.Dette;
import com.ism.data.entities.User;
import com.ism.data.enums.EtatDemandeDette;
import com.ism.services.IArticleService;
import com.ism.services.IClientService;
import com.ism.services.IDemandeArticleService;
import com.ism.services.IDemandeDetteService;
import com.ism.services.IDetteService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import com.ism.controllers.IDemandeDetteView;
import com.ism.controllers.IDetteView;
import com.ism.controllers.client.IApplicationClient;
import com.ism.controllers.implement.Application;
import com.ism.core.config.router.Router;
import com.ism.core.factory.IFactory;
import com.ism.core.factory.implement.Factory;
import com.ism.core.helper.Success;
import com.ism.core.helper.Tools;

public class ApplicationClient extends Application implements IApplicationClient, Initializable {
    @FXML
    private Label numDemandeDette;
    @FXML
    private Label numDette;
    @FXML
    private Label numDetteNonSolde;
    public Label infoConnect;
    private IFactory factory = Factory.getInstance();
    private IArticleService articleService;
    private IClientService clientService;
    private IDemandeDetteService demandeDetteService;
    private IDemandeDetteView demandeDetteView;
    private IDemandeArticleService demandeArticleService;
    private IDetteService detteService;
    private IDetteView detteView;
    private Scanner scanner = new Scanner(System.in);

    public ApplicationClient() {
        this.articleService = factory.getFactoryService().getInstanceArticleService();
        this.demandeDetteService = factory.getFactoryService().getInstanceDemandeDetteService();
        this.demandeDetteView = factory.getFactoryView().getInstanceDemandeDetteView();
        this.demandeArticleService = factory.getFactoryService().getInstanceDemandeArticleService();
        this.detteService = factory.getFactoryService().getInstanceDetteService();
        this.detteView = factory.getFactoryView().getInstanceDetteView();
        this.clientService = factory.getFactoryService().getInstanceClientService();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        this.articleService = factory.getFactoryService().getInstanceArticleService();
        this.demandeDetteService = factory.getFactoryService().getInstanceDemandeDetteService();
        this.demandeDetteView = factory.getFactoryView().getInstanceDemandeDetteView();
        this.demandeArticleService = factory.getFactoryService().getInstanceDemandeArticleService();
        this.detteService = factory.getFactoryService().getInstanceDetteService();
        this.detteView = factory.getFactoryView().getInstanceDetteView();
        this.clientService = factory.getFactoryService().getInstanceClientService();
        Success.showSuccessMsg("Message Connexion", "Vous êtes connecté avec succès.");
        if (infoConnect != null) {
            infoConnect.setText(Router.userParams);
        }
        this.loadDash();
    }

    public void loadDash() {
        Client client = new Client();
        client.setUser(Router.userConnect);
        this.numDetteNonSolde.setText("Dette Non Soldées ("+ detteService.findAllDetteNonSoldeForClient(client).size() +")");
        this.numDette.setText("Dettes ("+ detteService.findAllDetteForClient(client).size() +")");
        this.numDemandeDette.setText("Demandes Dettes ("+ demandeDetteService.findAllDemandeDettesForClient(client).size() +")");
    }

    @FXML
    public void loadDetteSold(ActionEvent e) throws IOException {
        Tools.loadPersist(e, "Liste Dette", "/com/ism/views/list.dette.solde.fxml");
        // Mettre à jour les labels une fois les services initialisés
        loadDash(); 
    }

    @FXML
    public void loadGestionDemandesDette(ActionEvent e) throws IOException {
        Tools.loadPersist(e, "Gestion Demandes Dette", "/com/ism/views/gestion.client.demande.dette.fxml");
        // Mettre à jour les labels une fois les services initialisés
        loadDash(); 
    }

    @FXML
    public void loadRelanceDemandeDette(ActionEvent e) throws IOException {
        Tools.loadPersist(e, "Relancement Demandes Dette", "/com/ism/views/relance.demande.dette.fxml");
        // Mettre à jour les labels une fois les services initialisés
        loadDash(); 
    }

    @FXML
    @Override
    public void logout(ActionEvent e) throws IOException {
        // Retourner à la page de connexion
        Tools.load(e, "Gestion Dette Boutiquier", "/com/ism/views/connexion.fxml");
    }

    @Override
    public int menu() {
        String choice;
        String[] validValues = { "1", "2", "3", "4", "5" };
        do {
            System.out.println("1- Lister des dettes non soldées");
            System.out.println("2- Faire une demande de dette");
            System.out.println("3- Lister des demandes de dette");
            System.out.println("4- Relancer une demande de dette annuler");
            System.out.println("5- Déconnexion");
            System.out.print(MSG_CHOICE);
            choice = scanner.nextLine();
            if (!Arrays.asList(validValues).contains(choice)) {
                System.out.println("Erreur, choix de l'index du menu invalide.");
            }
        } while (!Arrays.asList(validValues).contains(choice));
        return Integer.parseInt(choice);
    }

    @Override
    public void run(User user) {
        Integer choix;
        msgWelcome(user);
        do {
            choix = menu();
            switch (choix) {
                case 1:
                    displayDette(detteService, detteView);
                    break;
                case 2:
                    saisirDette(articleService, clientService, demandeDetteService, demandeDetteView, demandeArticleService, user);
                    break;
                case 3:
                    displayDemandeDette(demandeDetteService, demandeDetteView);
                    break;
                case 4:
                    relaunchDette(demandeDetteService, demandeDetteView);
                    break;
                default:
                    System.out.println(MSG_EXIT);
                    break;
            }
        } while (choix != 5);
    }

    @Override
    public void displayDette(IDetteService detteService, IDetteView detteView) {
        if (isEmpty(detteService.length(), "Aucun dette n'a été enregistré.")) {
            return;
        }
        motif('+');
        System.out.println("Choisissez l'id pour plus de detail");
        Dette dette = detteView.getObject(detteService.findAll());
        motif('+');
        subMenu(dette);
    }

    @Override
    public void displayPaiement(Dette dette) {
        dette.getPaiements().forEach(System.out::println);
    }

    @Override
    public void displayArticle(Dette dette) {
        for (Detail detail : dette.getDetails()) {
            System.out.println(detail.getArticle());
        }
    }

    @Override
    public void subMenu(Dette dette) {
        String choice;
        System.out.println("1- Voir les articles");
        System.out.println("2- Voir les paiements");
        do {
            System.out.print(MSG_CHOICE);
            choice = scanner.nextLine();
            if (choice.equals("1")) {
                displayArticle(dette);
                return;
            } else if (choice.equals("2")) {
                displayPaiement(dette);
                return;
            } else {
                System.out.println("Erreur, choix invalide.");
            }
        } while (!choice.equals("1") || !choice.equals("2"));
    }

    @Override
    public void saisirDette(IArticleService articleService, IClientService clientService,IDemandeDetteService demandeDetteService, IDemandeDetteView demandeDetteView, IDemandeArticleService demandeArticleService, User user) {
        DemandeDette dette = demandeDetteView.saisir(clientService, articleService, demandeArticleService, user);
        if (dette == null) {
            return;
        }
        demandeDetteService.add(dette);
        // transactionDemandeArticles(demandeArticleService, dette);
        msgSuccess("Demande de dette ajoutée avec succès.");
    }

    @Override
    public void displayDemandeDette(IDemandeDetteService demandeDetteService, IDemandeDetteView demandeDetteView) {
        if (isEmpty(demandeDetteService.length(), "Aucune demande de dette n'a été enregistrée.")) {
            return;
        }
        demandeDetteView.afficher(demandeDetteService.findAll());
        System.out.print("Voulez-vous filtrer les demandes de dette(O/N): ");
        char choix = scanner.nextLine().charAt(0);
        if (choix == 'O' || choix == 'o') {
            subMenuDemandeDette(demandeDetteService, demandeDetteView);
        }
        motif('+');
    }

    @Override
    public void subMenuDemandeDette(IDemandeDetteService demandeDetteService, IDemandeDetteView demandeDetteView) {
        String choice;
        System.out.println("Filtrer par: ");
        System.out.println("1- En cour la demande");
        System.out.println("2- Annuler la demande");
        System.out.print(MSG_CHOICE);
        choice = scanner.nextLine();
        if (choice.equals("1")) {
            List<DemandeDette> demandeDettes = demandeDetteService.findAll().stream().filter(dette -> dette.getEtat().name().compareTo("ENCOURS") == 0).collect(Collectors.toList());
            demandeDetteView.afficher(demandeDettes);
        } else if (choice.equals("2")) {
            List<DemandeDette> demandeDettes = demandeDetteService.findAll().stream().filter(dette -> dette.getEtat().name().compareTo("ANNULE") == 0).collect(Collectors.toList());
            demandeDetteView.afficher(demandeDettes);
        } else {
            System.out.println("Erreur, choix invalide.");
        }
    }

    @Override
    public void relaunchDette(IDemandeDetteService demandeDetteService, IDemandeDetteView demandeDetteView) {
        List<DemandeDette> demandeDettes = demandeDetteService.findAll().stream().filter(dette -> dette.getEtat().name().compareTo("ANNULE") == 0).collect(Collectors.toList());
        if (isEmpty(demandeDettes.size(), "Aucune demande de dette annulée n'a été trouvée.")) {
            return;
        }
        DemandeDette demandeDette = demandeDetteView.getObject(demandeDettes);
        demandeDette.setEtat(EtatDemandeDette.ENCOURS);
        demandeDetteService.update(demandeDettes, demandeDette);
        msgSuccess("Relancement de la demande de dette avec succès.");
    }
}
