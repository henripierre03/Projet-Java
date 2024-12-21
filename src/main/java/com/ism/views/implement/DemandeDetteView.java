package com.ism.views.implement;

import java.util.List;

import com.ism.data.entities.Article;
import com.ism.data.entities.Client;
import com.ism.data.entities.DemandeArticle;
import com.ism.data.entities.DemandeDette;
import com.ism.data.entities.User;
import com.ism.data.enums.EtatDemandeDette;
import com.ism.services.IArticleService;
import com.ism.services.IClientService;
import com.ism.services.IDemandeArticleService;
import com.ism.services.IDemandeDetteService;
import com.ism.views.IDemandeDetteView;

public class DemandeDetteView extends ImpView<DemandeDette> implements IDemandeDetteView {
    private IDemandeDetteService demandeDetteService;

    public DemandeDetteView(IDemandeDetteService demandeDetteService) {
        this.demandeDetteService = demandeDetteService;
    }

    @Override
    public DemandeDette saisir(IClientService clientService, IArticleService articleService, IDemandeArticleService demandeArticleService, User user) {
        List<Article> articleAvailable = articleService.findAllAvailable();
        if (articleAvailable.isEmpty()) {
            System.out.println("Aucun article n'a été enregistré.");
            return null;
        }
        DemandeDette demandeDette = initializeDemandeDette(clientService, user);
        String choice;
        do {
            displayAvailableArticles(articleAvailable);
            choice = getUserChoice();
            if (!choice.equals("0")) {
                processArticleChoice(choice, articleAvailable, articleService, demandeDette);
            }
        } while (!choice.equals("0"));

        // Add demande de dette à un client
        Client client = clientService.findBy(clientService.findAll(), demandeDette.getClient());
        client.addDemandeDette(demandeDette);
        // Transaction
        clientService.update(clientService.findAll(), client);
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
        System.out.println("Montant total: " + demandeDette.getMontantTotal() + " Franc CFA");
        System.out.println("État: " + demandeDette.getEtat());
        System.out.println("Client: " + (demandeDette.getClient() != null ? demandeDette.getClient().getSurname() : "N/A"));
        System.out.println("---Articles demandés---");
        for (DemandeArticle da : demandeDette.getDemandeArticles()) {
            System.out.println("  - Article : " + da.getArticle().getLibelle());
            System.out.println("  - Quantité : " + da.getQteArticle());
            System.out.println("  - Prix de vente : " + da.getArticle().getPrix());
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

    private void processArticleChoice(String choice, List<Article> articleAvailable, IArticleService articleService, DemandeDette demandeDette) {
        int quantity = getValidQuantity();
        if (quantity <= -1)
            return;

        Article article = findArticle(choice, articleAvailable, articleService);
        if (article == null)
            return;

        if (!checkStock(article, quantity))
            return;

        updateArticleStock(article, quantity);
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

    private void updateArticleStock(Article article, int quantity) {
        article.setQteStock(article.getQteStock() - quantity);
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
