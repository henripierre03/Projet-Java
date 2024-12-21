package com.ism.data.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ism.data.enums.EtatDette;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Dette extends AbstractEntity {
    private Double montantTotal;
    private Double montantVerser;
    private boolean status;
    private EtatDette etat;

    // Nav
    private Client client;
    private DemandeDette demandeDette;
    private List<Paiement> paiements;
    private List<Detail> details;

    public Dette(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Double montantTotal, Double montantVerser,
            boolean status, EtatDette etat, Client client, DemandeDette demandeDette) {
        super(id, createdAt, updatedAt);
        this.montantTotal = montantTotal;
        this.montantVerser = montantVerser;
        this.status = status;
        this.etat = etat;
        this.client = client;
        this.demandeDette = demandeDette;
    }

    public Dette() {
        super.createdAt = LocalDateTime.now();
        super.updatedAt = LocalDateTime.now();
        this.montantTotal = 0.0;
        this.montantVerser = 0.0;
    }

    public void addPaiement(Paiement paiement) {
        if (paiements == null) {
            paiements = new ArrayList<>();
        }
        paiements.add(paiement);
    }

    public void addDetail(Detail detail) {
        if (details == null) {
            details = new ArrayList<>();
        }
        details.add(detail);
    }

    public Double getMontantRestant() {
        return this.montantTotal - this.montantVerser;
    }

    @Override
    public String toString() {
        return "Dette [idDette=" + super.getId() + ", montantTotal=" + montantTotal + ", montantVerser=" + montantVerser
                + ", status=" + status + ", etat=" + etat + ", dateCreation=" + super.getCreatedAt() + ", client="
                + client
                + ", demandeDette=" + demandeDette + ", updateAt=" + super.getUpdatedAt() + "]";
    }
}
