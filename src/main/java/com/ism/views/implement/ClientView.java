package com.ism.views.implement;

import java.util.List;

import com.ism.core.helper.Helper;
import com.ism.data.entities.Client;
import com.ism.data.entities.DemandeDette;
import com.ism.data.entities.Dette;
import com.ism.services.IClientService;
import com.ism.views.IClientView;

public class ClientView extends ImpView<Client> implements IClientView {
    private IClientService clientService;

    public ClientView(IClientService clientService) {
        this.clientService = clientService;
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
        client.setTel(checkTel());
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

    private String checkTel() {
        String tel;
        do {
            System.out.print("Entrez le numéro de téléphone : ");
            tel = scanner.nextLine();
            if (tel.isBlank()) {
                System.out.println("Erreur, le numéro de téléphone ne peut être vide.");
            }
            // Vérifie si le numéro commence par 70, 77 ou 78, et contient 9 chiffres au total
            if (!tel.matches("(70|77|78)\\d{7}")) {
                System.out.println("Format incorrect. Le numéro doit commencer par 70, 77 ou 78 et contenir 9 chiffres au total (par exemple : 77 xxx xx xx).");
            }
        } while (tel.isBlank() || !tel.matches("(70|77|78)\\d{7}"));
        return "+221" + tel.trim();
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
        System.out.println("Cumul Montant Dû : " + client.getCumulMontantDu());
        System.out.println("Status : " + client.isStatus());
        System.out.println("User : " + (client.getUser() == null ? "N/A" : client.getUser()));
        if (client.getDemandeDettes() != null) {
            motif("-");
            System.out.println("Liste Demande de dette : ");
            for (DemandeDette dette : client.getDemandeDettes()) {
                System.out.println("Montant Total: " + dette.getMontantTotal());
                System.out.println("Date: " + dette.getId());
                System.out.println("Etat: " + dette.getEtat());
            }
        } else {
            System.out.println("Liste des demandes de dette : N/A");
        }
        if (client.getDettes() != null) {
            motif("-");
            System.out.println("Liste de dette : ");
            for (Dette dette : client.getDettes()) {
                System.out.println("Montant Total: " + dette.getMontantTotal());
                System.out.println("Montant Verser: " + dette.getMontantVerser());
                System.out.println("Montant Restant: " + dette.getMontantRestant());
                System.out.println("Status: " + dette.isStatus());
                System.out.println("État: " + dette.getEtat());
                System.out.println("Date: " + dette.getCreatedAt());
            }
        } else {
            System.out.println("Liste des dettes : N/A");
        }
        motif("+");
    }

    @Override
    public Client saisir() {
        // Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saisir'");
    }
}
