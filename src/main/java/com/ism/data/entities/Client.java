package com.ism.data.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false, exclude = {"demandeDettes", "dettes"})
public class Client extends AbstractEntity {
    private String surname;
    private String tel;
    private String address;
    private Double cumulMontantDu;
    private boolean status;

    // Navigabilité: revoir la pertinence de garder certain navigabilité
    private User user;
    private List<DemandeDette> demandeDettes;
    private List<Dette> dettes;

    public Client(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String surname, String tel, String address,
            Double cumulMontantDu, boolean status, User user) {
        super(id, createdAt, updatedAt);
        this.surname = surname;
        this.tel = tel;
        this.address = address;
        this.cumulMontantDu = cumulMontantDu;
        this.status = status;
        this.user = user;
    }

    public Client() {
        super();
        super.createdAt = LocalDateTime.now();
        super.updatedAt = LocalDateTime.now();
        this.cumulMontantDu = 0.0;
    }

    public void addDemandeDette(DemandeDette demandeDette) {
        if (demandeDettes == null) {
            demandeDettes = new ArrayList<>();
        }
        demandeDettes.add(demandeDette);
    }

    // Méthode modifiée pour mettre à jour le cumul lors de l'ajout d'une dette
    public void addDetteClient(Dette dette) {
        if (dettes == null) {
            dettes = new ArrayList<>();
        }
        dettes.add(dette);
        updateCumulMontantDu();
    }

    // Nouvelle méthode pour mettre à jour le cumul des dettes
    public void updateCumulMontantDu() {
        double newCumul = 0.0;
        if (dettes != null && !dettes.isEmpty()) {
            for (Dette dette : dettes) {
                newCumul += dette.getMontantRestant();
            }
        }
        this.cumulMontantDu = newCumul;
    }

    // Méthode modifiée pour obtenir le cumul actuel
    public Double getCumulMontantDu() {
        updateCumulMontantDu(); // Met à jour le cumul avant de le retourner
        return cumulMontantDu;
    }

    // Nouvelle méthode pour mettre à jour le cumul après un paiement
    public void updateCumulAfterPaiement(Dette dette, double montantPaye) {
        if (dette != null && dettes.contains(dette)) {
            updateCumulMontantDu();
        }
    }

    @Override
    public String toString() {
        return "Client [id="+ super.getId() +", surname=" + surname + ", tel=" + tel + ", address=" + address + ", cumulMontantDu="
                + cumulMontantDu + ", status=" + status + ", user=" + user + ", createAt=" + super.getCreatedAt()
                + ", updateAt=" + super.getUpdatedAt() + "]";
    }
}
