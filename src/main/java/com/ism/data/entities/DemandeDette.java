package com.ism.data.entities;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.ism.data.enums.EtatDemandeDette;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class DemandeDette extends AbstractEntity {
    private Double montantTotal;
    private EtatDemandeDette etat;

    // Nav
    private List<DemandeArticle> demandeArticles;
    private Dette dette;
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
        if (demandeArticles == null) {
            demandeArticles = new ArrayList<>();
        }
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
                + montantTotal + ", etat=" + etat + ", dette=" + dette + ", client=" + client + ", updateAt="
                + super.getUpdatedAt() + "]";
    }
}
