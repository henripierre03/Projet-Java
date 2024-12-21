package com.ism.data.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.ism.data.enums.EtatDette;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "dettes")
public class Dette extends AbstractEntity {
    @Column(name = "montant_total", nullable = false)
    private Double montantTotal;

    @Column(name = "montant_verser", nullable = false)
    private Double montantVerser;

    @Column(name = "status", nullable = false)
    private boolean status;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false)
    private EtatDette etat;

    // Relation avec l'entité Client
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Relation avec l'entité DemandeDette
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "demande_dette_id")
    private DemandeDette demandeDette;

    // Relation avec Paiement, cascade pour sauvegarder automatiquement les
    // paiements liés
    @OneToMany(mappedBy = "dette", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Paiement> paiements = new ArrayList<>();

    // Relation avec Detail
    @OneToMany(mappedBy = "dette", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Detail> details = new ArrayList<>();

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
        paiements.add(paiement);
    }

    public void addDetail(Detail detail) {
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
                + ", demandeDette=" + (demandeDette == null ? "N/A" : demandeDette.getEtat()) + ", updateAt="
                + super.getUpdatedAt() + "]";
    }
}
