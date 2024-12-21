package com.ism.data.entities;

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

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.ism.data.enums.EtatDemandeDette;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "demande_dettes")
public class DemandeDette extends AbstractEntity {
    @Column(name = "montant_total", nullable = false)
    private Double montantTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false)
    private EtatDemandeDette etat;

    @OneToMany(mappedBy = "demandeDette", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DemandeArticle> demandeArticles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dette_id")
    private Dette dette;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    public DemandeDette(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Double montantTotal,
            EtatDemandeDette etat, Dette dette, Client client) {
        super(id, createdAt, updatedAt);
        this.montantTotal = montantTotal;
        this.etat = etat;
        this.dette = dette;
        this.client = client;
    }

    public void addDemandeArticle(DemandeArticle demandeArticle) {
        demandeArticles.add(demandeArticle);
    }

    public DemandeDette() {
        super.createdAt = LocalDateTime.now();
        super.updatedAt = LocalDateTime.now();
        this.montantTotal = 0.0;
    }

    @Override
    public String toString() {
        return "DemandeDette [idDemandeDette=" + super.getId() + ", dateDemande=" + super.getCreatedAt()
                + ", montantTotal="
                + montantTotal + ", etat=" + etat + ", dette=" + (dette == null ? "N/A" : dette.getEtat()) + ", client=" + client + ", updateAt="
                + super.getUpdatedAt() + "]";
    }
}
