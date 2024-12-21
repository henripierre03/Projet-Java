package com.ism.controllers.implement;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.ism.data.entities.Client;
import com.ism.data.entities.Dette;
import com.ism.data.entities.Paiement;
import com.ism.services.IClientService;
import com.ism.services.IDetteService;
import com.ism.services.IPaiementService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import com.ism.controllers.IPaiementView;
import com.ism.core.factory.IFactory;
import com.ism.core.factory.implement.Factory;
import com.ism.core.helper.Errors;
import com.ism.core.helper.Success;

public class PaiementView extends ImpView<Paiement> implements IPaiementView, Initializable {
    private IFactory factory = Factory.getInstance();
    private IDetteService detteService;
    private IClientService clientService;
    private IPaiementService paiementService;
    // >> FXML
    public ComboBox<Client> comboClient;
    public ComboBox<Dette> comboDette;
    public TextField montantPayField;
    public TableView<Paiement> payTable;
    public TableColumn<Paiement, Integer> idCol;
    public TableColumn<Paiement, String> clientCol;
    public TableColumn<Paiement, Double> montantPayCol;
    public TableColumn<Paiement, String> montantRestantCol;
    public TableColumn<Paiement, String> montantTotalCol;
    // <<

    public PaiementView() {
        this.detteService = factory.getFactoryService().getInstanceDetteService();
        this.clientService = factory.getFactoryService().getInstanceClientService();
        this.paiementService = factory.getFactoryService().getInstancePaiementService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.detteService = factory.getFactoryService().getInstanceDetteService();
        this.clientService = factory.getFactoryService().getInstanceClientService();
        this.paiementService = factory.getFactoryService().getInstancePaiementService();
        setBtn();
        loadClient();
        loadPay();
    }

    public void setBtn() {
        if (comboDette == null)
            return;
        comboDette.setDisable(true);
    }

    @FXML
    public void addNewPay() {
        Stage stage = (Stage) comboClient.getScene().getWindow();
        if (comboClient.getValue() == null) {
            Errors.showCustomPopup(stage, "Erreur, veuillez sélectionner un client.");
            return;
        }
        Dette dette = comboDette.getValue();
        if (controllerInput(stage, dette)) {
            Object[] result = getPaiementClient(dette);
            Paiement paiement = (Paiement) result[0];
            // Update + Add paiement
            dette = (Dette) result[1];
            dette.addPaiement(paiement);
            // Mise à jour du cumul du client après le paiement
            Client client = dette.getClient();
            client.updateCumulMontantDu();
            // Update toutes les entités
            paiementService.add(paiement);
            clientService.update(clientService.findAll(), client);
            resetFormPay();
            loadPay();
            loadClient();
            Success.showCustomPopup(stage, "Paiement effectué avec succès!");
        }
    }

    public boolean controllerInput(Stage stage, Dette dette) {
        String montantPay = montantPayField.getText().replace(",", ".");
        if (montantPayField.getText().isBlank()) {
            Errors.showCustomPopup(stage, "Erreur, le montant ne doit pas être vide.");
            return false;
        } else if (!isDecimal(montantPayField.getText())) {
            Errors.showCustomPopup(stage, "Erreur, le montant doit être un nombre décimal.");
            return false;
        } else if (Double.parseDouble(montantPay) > dette.getMontantTotal()) {
            Errors.showCustomPopup(stage, "Erreur, le montant payé ne peut pas être supérieur au montant total.");
            return false;
        }
        return true;
    }

    private Object[] getPaiementClient(Dette dette) {
        Paiement paiement = new Paiement();
        paiement.setMontantPaye(Double.parseDouble(montantPayField.getText()));
        paiement.setDette(dette);
        dette.setMontantVerser(dette.getMontantVerser() + paiement.getMontantPaye());
        return new Object[] { paiement, dette };
    }

    @FXML
    public void resetFormPay() {
        if (comboClient.getValue() == null)
            return;
        comboClient.setValue(null);
        comboDette.setValue(null);
        comboDette.setDisable(true);
        montantPayField.clear();
    }

    public void loadClient() {
        if (comboClient == null)
            return;
        ObservableList<Client> clientsFX = FXCollections.observableArrayList();
        List<Client> clients = clientService.getAllActifsWithAccount();
        for (Client c : clients) {
            clientsFX.add(c);
        }
        comboClient.setItems(clientsFX);
    }

    @FXML
    public void loadDette() {
        if (comboDette == null && comboDette.isDisable())
            return;
        Client client = comboClient.getValue();
        ObservableList<Dette> dettesFX = FXCollections.observableArrayList();
        List<Dette> dettes = detteService.findAllBy(client);
        if (dettes.isEmpty()) {
            comboDette.setDisable(true);
            return;
        } else {
            comboDette.setDisable(false);
        }
        for (Dette c : dettes) {
            dettesFX.add(c);
        }
        comboDette.setItems(dettesFX);
        // Configurez l'affichage des articles dans le ComboBox
        comboDette.setCellFactory(param -> new ListCell<Dette>() {
            @Override
            protected void updateItem(Dette item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getClient().getSurname() + " - Restant : " + item.getMontantRestant() + " FCFA");
                }
            }
        });
    }

    public void loadPay() {
        if (payTable == null)
            return;
        ObservableList<Paiement> payFX = FXCollections.observableArrayList();
        List<Paiement> pay = paiementService.findAll();
        for (Paiement u : pay) {
            payFX.add(u);
        }
        payTable.setItems(payFX);
        // >> Initialiser les colonnes de la table payTable ici
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getDette().getClient() != null
                        ? cellData.getValue().getDette().getClient().getSurname()
                        : "N/A"));
        montantPayCol.setCellValueFactory(new PropertyValueFactory<>("montantPaye"));
        montantRestantCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDette() != null
                ? String.valueOf(cellData.getValue().getDette().getMontantRestant())
                : "N/A"));
        montantTotalCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDette() != null
                ? String.valueOf(cellData.getValue().getDette().getMontantTotal())
                : "N/A"));
        // <<
    }

    @Override
    public Paiement saisir() {
        Paiement paiement = new Paiement();
        paiement.setMontantPaye(checkMontant("Entrer le montant du paiement: "));
        return paiement;
    }

    @Override
    public Paiement getObject(List<Paiement> list) {
        Paiement paiement;
        String choix;
        this.afficher(list);
        do {
            paiement = new Paiement();
            System.out.print("Choisissez une paiement par son id: ");
            choix = scanner.nextLine();
            if (isInteger(choix)) {
                paiement.setId(Long.parseLong(choix));
                paiement = paiementService.findBy(paiementService.findAll(), paiement);
            } else {
                continue;
            }
            if (paiement == null) {
                System.out.println("Erreur, l'id est invalide.");
            }

        } while (paiement == null);
        return paiement;
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
}
