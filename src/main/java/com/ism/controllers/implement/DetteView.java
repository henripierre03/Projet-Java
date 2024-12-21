package com.ism.controllers.implement;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.ism.data.entities.Client;
import com.ism.data.entities.Detail;
import com.ism.data.entities.Dette;
import com.ism.data.entities.Paiement;
import com.ism.data.enums.EtatDette;
import com.ism.services.IDetteService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import com.ism.controllers.IDetteView;
import com.ism.core.config.router.Router;
import com.ism.core.factory.IFactory;
import com.ism.core.factory.implement.Factory;
import com.ism.core.helper.Success;

public class DetteView extends ImpView<Dette> implements IDetteView, Initializable {
    private IFactory factory = Factory.getInstance();
    private IDetteService detteService;
    // >>Chargement des éléments: gestion de client
    @FXML
    private Button btnArchive;
    @FXML
    private TableView<Dette> detteTable;
    public TableView<Dette> detteNonSoldTable;
    @FXML
    private TableColumn<Dette, Integer> idDetteCol;
    @FXML
    private TableColumn<Dette, String> clientDetteCol;
    @FXML
    private TableColumn<Dette, Double> montantTotalDetteCol;
    @FXML
    private TableColumn<Dette, Double> montantVerserDetteCol;
    @FXML
    private TableColumn<Dette, Double> montantRestantDetteCol;
    @FXML
    private TableColumn<Dette, String> etatDetteCol;
    @FXML
    private TableColumn<Dette, String> stateDetteCol;
    // <<
    private Long detteId;

    public DetteView(IDetteService detteService) {
        this.detteService = factory.getFactoryService().getInstanceDetteService();
    }

    public DetteView() {
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        this.detteService = factory.getFactoryService().getInstanceDetteService();
        load();
        loadDetteNonSold();
    }

    public void load() {
        if (detteTable == null)
            return;
        ObservableList<Dette> dettesFX = FXCollections.observableArrayList();
        List<Dette> dettes = detteService.getAllSoldes();
        for (Dette u : dettes) {
            
            dettesFX.add(u);
        }
        detteTable.setItems(dettesFX);
        // >>Initialiser les colonnes de la table usersTable ici
        idDetteCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientDetteCol.setCellValueFactory(new PropertyValueFactory<>("client"));
        montantTotalDetteCol.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        montantVerserDetteCol.setCellValueFactory(new PropertyValueFactory<>("montantVerser"));
        montantRestantDetteCol.setCellValueFactory(new PropertyValueFactory<>("montantRestant"));
        etatDetteCol.setCellValueFactory(new PropertyValueFactory<>("etat"));
        stateDetteCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        // <<
    }

    public void loadDetteNonSold() {
        if (detteNonSoldTable == null)
            return;
        ObservableList<Dette> dettesFX = FXCollections.observableArrayList();
        Client client = new Client();
        client.setUser(Router.userConnect);
        List<Dette> dettes = detteService.findAllDetteNonSoldeForClient(client);
        for (Dette u : dettes) {
            dettesFX.add(u);
        }
        detteNonSoldTable.setItems(dettesFX);
        // >>Initialiser les colonnes de la table usersTable ici
        idDetteCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        montantTotalDetteCol.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        montantVerserDetteCol.setCellValueFactory(new PropertyValueFactory<>("montantVerser"));
        montantRestantDetteCol.setCellValueFactory(new PropertyValueFactory<>("montantRestant"));
        etatDetteCol.setCellValueFactory(new PropertyValueFactory<>("etat"));
        stateDetteCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        // <<
    }

    @FXML
    public void update(ActionEvent e) {
        // Récupérer le Stage actuel à partir de n'importe quel élément de la scène
        Stage stage = (Stage) btnArchive.getScene().getWindow();
        Dette d = new Dette();
        if (detteId == null)
            return;
        d.setId(detteId);
        Dette dette = detteService.findBy(d);
        if (dette.isStatus()) {
            dette.setStatus(false);
        } else {
            dette.setStatus(true);
        }
        detteService.update(dette);
        load();
        Success.showCustomPopup(stage, "Etat de la dette modifié avec succès.");
    }

    @FXML
    public void tableClicked(MouseEvent e) {
        Dette dette = detteTable.getSelectionModel().getSelectedItem();
        detteId = dette.getId();
        if (dette.isStatus()) {
            btnArchive.setText("Désactiver");
        } else {
            btnArchive.setText("Activer");
        }
    }

    @Override
    public Dette saisir() {
        Dette dette = new Dette();
        dette.setMontantTotal(checkMontant("Entrez le montant total: "));
        dette.setMontantVerser(checkMontant("Entrez le montant verser: "));
        dette.setStatus(true);
        dette.setEtat(EtatDette.ENCOURS);
        return dette;
    }

    @Override
    public Dette getObject(List<Dette> dettes) {
        Dette dette;
        String choix;
        this.display(dettes);
        do {
            dette = new Dette();
            System.out.print("Choisissez une dette par son id: ");
            choix = scanner.nextLine();
            if (isInteger(choix)) {
                dette.setId(Long.parseLong(choix));
                dette = detteService.findBy(dettes, dette);
            } else {
                continue;
            }
            if (dette == null) {
                System.out.println("Erreur, l'id est invalide.");
            }
        } while (dette == null);
        return dette;
    }
    
    private Double checkMontant(String msg) {
        String montant;
        do {
            System.out.print(msg);
            montant = scanner.nextLine();
            if (montant.isBlank()) {
                System.out.println("Erreur, le montant est vide.");
                continue;
            }
            if (!isDecimal(montant)) {
                System.out.println("Format incorrect, le montant doit être un nombre.");
                continue;
            }
            if (Double.parseDouble(montant) <= 0.0) {
                System.out.println("Format incorrect, le montant doit être positif.");
                continue;
            }
            // Si toutes les validations sont passées, sortir de la boucle
            break;
        } while (true);
        return Double.parseDouble(montant);
    }

    @Override
    public void display(List<Dette> dettes) {
        motif("+");
        System.out.println("Liste des dettes: ");
        motif("+");
        for (Dette dette : dettes) {
            displayDette(dette);
        }
    }

    @Override
    public void displayDette(Dette dette) {
        System.out.println("ID: " + dette.getId());
        System.out.println("Montant Total: " + dette.getMontantTotal() + "Franc CFA");
        System.out.println("Montant Verser: " + dette.getMontantVerser() + "Franc CFA");
        System.out.println("Montant Restant: " + dette.getMontantRestant() + "Franc CFA");
        System.out.println("Status: " + dette.isStatus());
        System.out.println("Etat: " + dette.getEtat());
        System.out.println("Date de contraction: " + dette.getCreatedAt());
        System.out.println("Client: " + dette.getClient().getSurname());
        System.out.println("Demande de dette: " + (dette.getDemandeDette() == null ? "N/A" : dette.getDemandeDette()));
        motif("-");
        displayPay(dette);
        motif("-");
        displayDetail(dette);
        motif("+");
    }
    
    @Override
    public void displayPay(Dette dette) {
        if (dette.getPaiements() != null && dette.getPaiements().isEmpty()) {
            System.out.println("Pas de paiements pour cette dette.");
            return;
        }
        System.out.println("---Paiements---");
        for (Paiement paiement : dette.getPaiements()) {
            System.out.println("  - Montant : " + paiement.getMontantPaye() + "Franc CFA");
            System.out.println("  - Date : " + paiement.getCreatedAt());
            motif("-");
        }
    }

    @Override
    public void displayDetail(Dette dette) {
        if (dette.getDetails() != null && dette.getDetails().isEmpty()) {
            System.out.println("Pas de détails pour cette dette.");
            return;
        }
        System.out.println("---Articles---");
        for (Detail detail : dette.getDetails()) {
            System.out.println("  - Article : " + detail.getArticle().getLibelle());
            System.out.println("  - Quantité : " + detail.getQte());
            System.out.println("  - Prix de vente : " + detail.getPrixVente() + "Franc CFA");
            motif("-");
        }
    }
}
