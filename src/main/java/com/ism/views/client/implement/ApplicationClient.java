package com.ism.views.client.implement;

import java.util.Arrays;
import java.util.Scanner;
import java.util.List;
import java.util.stream.Collectors;

import com.ism.data.entities.DemandeArticle;
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
import com.ism.views.IDemandeDetteView;
import com.ism.views.IDetteView;
import com.ism.views.client.IApplicationClient;
import com.ism.views.implement.Application;

public class ApplicationClient extends Application implements IApplicationClient {
    private final IArticleService articleService;
    private final IClientService clientService;
    private final IDemandeDetteService demandeDetteService;
    private final IDemandeDetteView demandeDetteView;
    private final IDemandeArticleService demandeArticleService;
    private final IDetteService detteService;
    private final IDetteView detteView;
    private final Scanner scanner;

    public ApplicationClient(IArticleService articleService, IClientService clientService, IDemandeDetteService demandeDetteService, IDemandeDetteView demandeDetteView, IDemandeArticleService demandeArticleService, IDetteService detteService, IDetteView detteView, Scanner scanner) {
        this.articleService = articleService;
        this.demandeDetteService = demandeDetteService;
        this.demandeDetteView = demandeDetteView;
        this.demandeArticleService = demandeArticleService;
        this.detteService = detteService;
        this.detteView = detteView;
        this.clientService = clientService;
        this.scanner = scanner;
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
        transactionDemandeArticles(demandeArticleService, dette);
        msgSuccess("Demande de dette ajoutée avec succès.");
    }

    private void transactionDemandeArticles(IDemandeArticleService demandeArticleService, DemandeDette demandeDette) {
        for (DemandeArticle a : demandeDette.getDemandeArticles()) {
            demandeArticleService.add(a);
        }
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
