package com.ism.views.implement;

import java.util.List;

import com.ism.data.entities.Detail;
import com.ism.data.entities.Dette;
import com.ism.data.entities.Paiement;
import com.ism.data.enums.EtatDette;
import com.ism.services.IDetteService;
import com.ism.views.IDetteView;

public class DetteView extends ImpView<Dette> implements IDetteView {
    private IDetteService detteService;

    public DetteView(IDetteService detteService) {
        this.detteService = detteService;
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
        System.out.println("Montant Total: " + dette.getMontantTotal());
        System.out.println("Montant Verser: " + dette.getMontantVerser());
        System.out.println("Montant Restant: " + dette.getMontantRestant());
        System.out.println("Status: " + dette.isStatus());
        System.out.println("Etat: " + dette.getEtat());
        System.out.println("Date de contraction: " + dette.getCreatedAt());
        System.out.println("Client: " + dette.getClient().getSurname());
        System.out.println("Demande de dette: " + (dette.getDemandeDette() == null ? "N/A" : dette.getDemandeDette()));
        motif("-");
        if (dette.getPaiements() != null) {
            displayPay(dette);
        } else {
            System.out.println("Pas de paiements pour cette dette.");
        }
        motif("-");
        if (dette.getDetails() != null) {
            displayDetail(dette);
        } else {
            System.out.println("Pas d'articles pour cette dette.");
        }
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
            System.out.println("  - Montant : " + paiement.getMontantPaye());
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
            System.out.println("  - Prix de vente : " + detail.getPrixVente());
            motif("-");
        }
    }
}
