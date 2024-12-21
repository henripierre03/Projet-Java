package com.ism.views.store.implement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Scanner;

import com.ism.data.entities.DemandeDette;
import com.ism.data.entities.Detail;
import com.ism.data.entities.Dette;
import com.ism.data.entities.Paiement;
import com.ism.data.entities.Article;
import com.ism.data.entities.Client;
import com.ism.data.entities.DemandeArticle;
import com.ism.data.entities.User;
import com.ism.data.enums.EtatDemandeDette;
import com.ism.data.enums.EtatDette;
import com.ism.services.IArticleService;
import com.ism.services.IClientService;
import com.ism.services.IDemandeDetteService;
import com.ism.services.IDetailService;
import com.ism.services.IDetteService;
import com.ism.services.IPaiementService;
import com.ism.services.IUserService;
import com.ism.views.IClientView;
import com.ism.views.IDemandeDetteView;
import com.ism.views.IDetteView;
import com.ism.views.IPaiementView;
import com.ism.views.IUserView;
import com.ism.views.implement.Application;
import com.ism.views.store.IApplicationStorekeeper;

public class ApplicationStorekeeper extends Application implements IApplicationStorekeeper {
    private final IArticleService articleService;
    private final IClientService clientService;
    private final IClientView clientView;
    private final IDemandeDetteService demandeDetteService;
    private final IDemandeDetteView demandeDetteView;
    private final IDetailService detailService;
    private final IDetteService detteService;
    private final IDetteView detteView;
    private final IPaiementService paiementService;
    private final IPaiementView paiementView;
    private final IUserService userService;
    private final IUserView userView;

    private final Scanner scanner;

    public ApplicationStorekeeper(IArticleService articleService,
            IClientService clientService, IClientView clientView, IDemandeDetteService demandeDetteService,
            IDemandeDetteView demandeDetteView, IDetailService detailService,
            IDetteService detteService, IDetteView detteView, IPaiementService paiementService,
            IPaiementView paiementView, IUserService userService, IUserView userView, Scanner scanner) {
        this.articleService = articleService;
        this.clientService = clientService;
        this.clientView = clientView;
        this.demandeDetteService = demandeDetteService;
        this.demandeDetteView = demandeDetteView;
        this.detailService = detailService;
        this.detteService = detteService;
        this.detteView = detteView;
        this.paiementService = paiementService;
        this.paiementView = paiementView;
        this.userService = userService;
        this.userView = userView;
        this.scanner = scanner;
    }

    @Override
    public int menu() {
        String choice;
        String[] validValues = { "1", "2", "3", "4", "5", "6", "7", "8" };
        do {
            System.out.println("1- Créer un client");
            System.out.println("2- Lister les clients");
            System.out.println("3- Rechercher un client");
            System.out.println("4- Créer une dette");
            System.out.println("5- Créer une paiement");
            System.out.println("6- Lister les dettes non soldées");
            System.out.println("7- Lister les demandes de dette en cours");
            System.out.println("8- Déconnexion");
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
                    saisirClient(clientService, clientView, userService, userView);
                    break;
                case 2:
                    displayClient(clientService, clientView);
                    break;
                case 3:
                    searchClientByTel(clientService, clientView);
                    break;
                case 4:
                    saisirDette(articleService, clientService, clientView, detteService, detailService, paiementView);
                    break;
                case 5:
                    saisirPaiement(paiementService, paiementView, detteService, detteView);
                    break;
                case 6:
                    displayDetteNonSolde(clientService, clientView, detteService, detteView);
                    break;
                case 7:
                    displayDemandeDette(articleService, detteService, demandeDetteService, demandeDetteView,
                            detailService);
                    break;
                default:
                    break;
            }
        } while (choix != 8);
    }

    public void saisirClient(IClientService clientService, IClientView clientView, IUserService userService,
            IUserView userView) {
        Client client = clientView.saisir(clientService);
        if (client == null) {
            return;
        }
        System.out.print("Voulez-vous enregistrer un compte utilisateur(O/N): ");
        char choix = scanner.nextLine().charAt(0);
        if (choix == 'O' || choix == 'o') {
            User user = userView.accountCustomer(userService, client);
            if (user == null) {
                return;
            }
            userService.add(user);
            client.setUser(user);
        }
        clientService.add(client);
        msgSuccess(MSG_ACCOUNT);
    }

    public void displayClient(IClientService clientService, IClientView clientView) {
        if (isEmpty(clientService.length(), MSG_CLIENT)) {
            return;
        }
        motif('+');
        clientView.display(clientService.findAll());
        System.out.print("Voulez-vous filtrer les clients avec compte ou sans compte(O/N): ");
        char choix = scanner.nextLine().charAt(0);
        if (choix == 'O' || choix == 'o') {
            subMenuClient(clientService, clientView);
        } else if (!(choix == 'N' || choix == 'n')) {
            System.out.println(MSG_ERROR);
        }
    }

    @Override
    public void subMenuClient(IClientService clientService, IClientView clientView) {
        String choice;
        System.out.println(MSG_FILTER);
        System.out.println("1- Un compte");
        System.out.println("2- Pas de compte");
        System.out.print(MSG_CHOICE);
        choice = scanner.nextLine();
        if (choice.equals("1")) {
            List<Client> clients = clientService.findAll().stream()
                    .filter(cl -> cl.getUser() != null)
                    .collect(Collectors.toList());
            if (isEmpty(clients.size(), MSG_CLIENT)) {
                return;
            }
            motif('+');
            clientView.display(clients);
        } else if (choice.equals("2")) {
            List<Client> clients = clientService.findAll().stream()
                    .filter(cl -> cl.getUser() == null)
                    .collect(Collectors.toList());
            if (isEmpty(clients.size(), MSG_CLIENT)) {
                return;
            }
            motif('+');
            clientView.display(clients);
        } else {
            System.out.println(MSG_ERROR);
        }
    }

    public void searchClientByTel(IClientService clientService, IClientView clientView) {
        if (isEmpty(clientService.length(), MSG_CLIENT)) {
            return;
        }
        Client clientSearch = new Client();
        clientView.display(clientService.findAll());
        clientSearch.setTel(checkTel());
        Client client = clientService.findBy(clientService.findAll(), clientSearch);
        clientView.displayClient(client);
        motif('+');
    }

    private String checkTel() {
        String tel;
        do {
            System.out.print("Entrer le tel du client à rechercher: ");
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

    public void saisirDette(IArticleService articleService, IClientService clientService,
            IClientView clientView, IDetteService detteService, IDetailService detailService, IPaiementView paiementView) {
        List<Article> articleAvailable = articleService.findAllAvailable();
        List<Client> clients = clientService.findAll();
        if (articleAvailable.isEmpty()) {
            System.out.println("Aucun article n'a été enregistré.");
            return;
        }
        if (clients.isEmpty()) {
            System.out.println("Aucun client n'a été enregistré.");
            return;
        }

        Client client = clientView.getObject(clientService.findAll());
        Dette dette = new Dette();
        Dette testDette;
        dette.setClient(client);
        String choice;
        do {
            displayAvailableArticles(articleAvailable);
            choice = getUserChoice();
            if (!choice.equals("0")) {
                testDette = processArticleChoice(articleService, choice, articleAvailable, dette);
                if (testDette != null) {
                    dette = testDette;
                }
            }
        } while (!choice.equals("0"));
        System.out.print("Voulez-vous enregistrer un(des) paiement(s) (O/N): ");
        char choicePay = scanner.nextLine().charAt(0);
        if (choicePay == 'O' || choicePay == 'o') {
            Object[] result = getPaiementClient(paiementView, dette);
            Paiement paiement = (Paiement) result[0];
            dette = (Dette) result[1];
            dette.addPaiement(paiement);
            // Mise à jour du cumul après le paiement
            client.updateCumulAfterPaiement(dette, paiement.getMontantPaye());
        }
        // Transaction
        client.addDetteClient(dette);
        detteService.add(dette);
        transactionDetail(detailService, dette);
        // Mise à jour du client dans la base de données
        client.updateCumulMontantDu();
        clientService.update(clientService.findAll(), client);
        msgSuccess("Dette effectué avec succès!");
    }

    private void transactionDetail(IDetailService detailService, Dette dette) {
        for (Detail d : dette.getDetails()) {
            detailService.add(d);
        }
    }

    private Object[] getPaiementClient(IPaiementView paiementView, Dette dette) {
        Paiement paiement;
        do {
            paiement = paiementView.saisir();
            if (paiement.getMontantPaye() > dette.getMontantTotal()) {
                System.out.println("Erreur, le montant payé dépasse le montant total de la dette.");
            }
        } while (paiement.getMontantPaye() > dette.getMontantTotal());
        paiement.setDette(dette);
        dette.setMontantVerser(dette.getMontantVerser() + paiement.getMontantPaye());
        return new Object[] { paiement, dette };
    }

    private void displayAvailableArticles(List<Article> articleAvailable) {
        articleAvailable.forEach(System.out::println);
    }

    private String getUserChoice() {
        System.out.print("Entrez l'id de l'article(0 pour terminer): ");
        return scanner.nextLine();
    }

    private Dette processArticleChoice(IArticleService articleService, String choice, List<Article> articleAvailable,
            Dette dette) {
        int quantity = getValidQuantity();
        if (quantity <= -1)
            return null;

        Article article = findArticle(choice, articleAvailable);
        if (article == null)
            return null;

        if (!checkStock(article, quantity))
            return null;

        updateArticleStock(articleService, article, quantity);
        return addDemandeArticle(article, quantity, dette);
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

    private Article findArticle(String id, List<Article> articleAvailable) {
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
            System.out.println("Quantité saisit supérieur à celui en stock.");
            return false;
        }
        return true;
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

    public void saisirPaiement(IPaiementService paiementService, IPaiementView paiementView, IDetteService detteService,
            IDetteView detteView) {
        List<Dette> dettes = detteService.findAll();
        if (dettes.isEmpty()) {
            System.out.println("Aucune dette n'a été enregistré.");
            return;
        }

        Dette dette = detteView.getObject(dettes);
        Object[] result = getPaiementClient(paiementView, dette);
        Paiement paiement = (Paiement) result[0];
        // Update + Add paiement
        dette = (Dette) result[1];
        dette.addPaiement(paiement);
        // Mise à jour du cumul du client après le paiement
        Client client = dette.getClient();
        client.updateCumulAfterPaiement(dette, paiement.getMontantPaye());
        // Update toutes les entités
        detteService.update(dette);
        paiementService.add(paiement);
        clientService.update(clientService.findAll(), client);
        msgSuccess("Paiement effectué avec succès!");
    }

    public void displayDetteNonSolde(IClientService clientService, IClientView clientView, IDetteService detteService,
            IDetteView detteView) {
        List<Dette> dettes = detteService.getAllNonSoldes();
        if (dettes.isEmpty()) {
            System.out.println("Aucune dette non soldé n'a été enregistrée.");
            return;
        }
        motif('-');
        System.out.println("Filtrer une dette");
        motif('-');
        Client client = clientView.getObject(clientService.findAll());
        subMenu(detteView, client);
    }

    private void subMenu(IDetteView detteView, Client client) {
        String choice;
        System.out.println("1- Voir les articles");
        System.out.println("2- Voir les paiements");
        System.out.print(MSG_CHOICE);
        choice = scanner.nextLine();
        if (choice.equals("1")) {
            if (isEmpty(client.getDettes().size(), "Aucune article n'a été enregistrée.")) {
                return;
            }
            for (Dette dette : client.getDettes()) {
                detteView.displayDetail(dette);
            }
        } else if (choice.equals("2")) {
            if (isEmpty(client.getDettes().size(), "Aucune paiement n'a été enregistrée.")) {
                return;
            }
            for (Dette dette : client.getDettes()) {
                detteView.displayPay(dette);
            }
        } else {
            System.out.println("Erreur, choix invalide.");
        }
    }

    public void displayDemandeDette(IArticleService articleService, IDetteService detteService,
            IDemandeDetteService demandeDetteService, IDemandeDetteView demandeDetteView,
            IDetailService detailService) {
        List<DemandeDette> demandeDettesEnCours = demandeDetteService.findAll().stream()
                .filter(dette -> dette.getEtat().name().compareTo("ENCOURS") == 0).collect(Collectors.toList());
        if (isEmpty(demandeDettesEnCours.size(), "Aucune demande de dette n'a été enregistrée.")) {
            return;
        }
        System.out.println("1- filtrer les demandes de dette");
        System.out.println("2- Voir une demande de dette");
        System.out.print("Voulez-vous : ");
        String choice = scanner.nextLine();
        if (choice.equals("1")) {
            subMenuDemandeDette(demandeDetteService, demandeDetteView);
        } else if (choice.equals("2")) {
            DemandeDette demandeDette = demandeDetteView.getObject(demandeDettesEnCours);
            
            demandeDetteView.afficherDemandeDette(demandeDette);
            motif('+');
            askDemandeDette(articleService, detteService, demandeDetteService, demandeDette, detailService, clientService);
        } else {
            System.out.println(MSG_ERROR);
        }
    }

    private void subMenuDemandeDette(IDemandeDetteService demandeDetteService, IDemandeDetteView demandeDetteView) {
        String choice;
        System.out.println(MSG_FILTER);
        System.out.println("1- En cour la demande");
        System.out.println("2- Annuler la demande");
        System.out.print(MSG_CHOICE);
        choice = scanner.nextLine();
        if (choice.equals("1")) {
            List<DemandeDette> demandeDettes = demandeDetteService.findAll().stream()
                    .filter(dette -> dette.getEtat().name().compareTo("ENCOURS") == 0).collect(Collectors.toList());
            if (isEmpty(demandeDetteService.length(), "Aucune demande de dette (En cour) n'a été enregistrée.")) {
                return;
            }
            demandeDetteView.afficher(demandeDettes);

        } else if (choice.equals("2")) {
            List<DemandeDette> demandeDettes = demandeDetteService.findAll().stream()
                    .filter(dette -> dette.getEtat().name().compareTo("ANNULE") == 0).collect(Collectors.toList());
            if (isEmpty(demandeDetteService.length(), "Aucune demande de dette (Annulée) n'a été enregistrée.")) {
                return;
            }
            demandeDetteView.afficher(demandeDettes);
        } else {
            System.out.println("Erreur, choix invalide.");
        }
    }

    private void askDemandeDette(IArticleService articleService, IDetteService detteService, IDemandeDetteService demandeDetteService,
            DemandeDette demandeDette, IDetailService detailService, IClientService clientService) {
        System.out.print("Voulez-vous Annuler ou Accepter la demande de dette(O/N): ");
        char ask = scanner.nextLine().charAt(0);
        if (ask == 'O' || ask == 'o') {
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
            transactionDetail(detailService, dette);
            // Update dette client
            Client client = demandeDette.getClient();
            client.addDetteClient(dette);
            clientService.update(clientService.findAll(), client);
            msgSuccess("Demande de dette accepter avec succès.");
        } else if (ask == 'n' || ask == 'N') {
            demandeDette.setEtat(EtatDemandeDette.ANNULE);
            demandeDetteService.update(demandeDetteService.findAll(), demandeDette);
            msgSuccess("Demande de dette refuser avec succès.");
        }
        else {
            System.out.println(MSG_ERROR);
        }
    }
}
