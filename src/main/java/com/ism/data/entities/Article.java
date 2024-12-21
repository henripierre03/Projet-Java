package com.ism.data.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Article extends AbstractEntity {
    private String libelle;
    private Double prix;
    private Integer qteStock;

    // Nav ==> ce n'est pas utile
    private List<Detail> details;
    private List<DemandeArticle> demandeArticles;

    public Article() {
        super.createdAt = LocalDateTime.now();
        super.updatedAt = LocalDateTime.now();
    }

    public Article(long id, String libelle, double prix, int qteStock, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.libelle = libelle;
        this.prix = prix;
        this.qteStock = qteStock;
    }

    public void addDetail(Detail detail) {
        if (demandeArticles == null) {
            details = new ArrayList<>();
        }
        details.add(detail);
    }

    public void addDemandeArticle(DemandeArticle demandeArticle) {
        demandeArticles.add(demandeArticle);
    }

    @Override
    public String toString() {
        return "Article [id=" + super.getId() + ", libelle=" + libelle + ", prix=" + prix + ", qteStock=" + qteStock
                + ", createAt=" + super.getCreatedAt() + ", updateAt=" + super.getUpdatedAt()
                + "]";
    }
}
