package com.ism.data.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "articles")
public class Article extends AbstractEntity {
    @Column(name = "libelle", nullable = false)
    private String libelle;
    
    @Column(name = "prix", nullable = false)
    private Double prix;
    
    @Column(name = "qte_stock", nullable = false)
    private Integer qteStock;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Detail> details = new ArrayList<>();
    
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DemandeArticle> demandeArticles = new ArrayList<>();


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
