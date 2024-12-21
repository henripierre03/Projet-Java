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
@Table(name = "demande_articles")
public class DemandeArticle extends AbstractEntity {
    @Column(name = "qte_article", nullable = false)
    private int qteArticle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "demande_dette_id", nullable = false)
    private DemandeDette demandeDette;

    public DemandeArticle(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, int qteArticle, Article article,
            DemandeDette demandeDette) {
        super(id, createdAt, updatedAt);
        this.qteArticle = qteArticle;
        this.article = article;
        this.demandeDette = demandeDette;
    }

    public DemandeArticle() {
        super.createdAt = LocalDateTime.now();
        super.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "DemandeArticle [idDemandeArticle=" + super.getId() + ", qteArticle=" + qteArticle + ", article="
                + article + ", demandeDette=" + demandeDette + ", createAt=" + super.getCreatedAt() + ", updateAt="
                + super.getUpdatedAt() + "]";
    }
}
