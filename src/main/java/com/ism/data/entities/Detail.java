package com.ism.data.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "details")
public class Detail extends AbstractEntity {

    @Column(name = "qte", nullable = false)
    private int qte;

    @Column(name = "prix_vente", nullable = false)
    private Double prixVente;

    // Relation avec l'entité Article
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    // Relation avec l'entité Dette
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dette_id", nullable = false)
    private Dette dette;

    public Detail(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, int qte, Double prixVente, Article article,
            Dette dette) {
        super(id, createdAt, updatedAt);
        this.qte = qte;
        this.prixVente = prixVente;
        this.article = article;
        this.dette = dette;
    }

    public Detail() {
        super.createdAt = LocalDateTime.now();
        super.updatedAt = LocalDateTime.now();
        this.qte = 0;
        this.prixVente = 0.0;
    }

    @Override
    public String toString() {
        return "Detail [idDetteArticle=" + super.getId() + ", qte=" + qte + ", prixVente=" + prixVente + ", article="
                + article + ", dette=" + dette + ", createAt=" + super.getCreatedAt() + ", updateAt="
                + super.getUpdatedAt() + "]";
    }
}
