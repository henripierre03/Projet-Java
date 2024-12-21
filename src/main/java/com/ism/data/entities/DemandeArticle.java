package com.ism.data.entities;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class DemandeArticle extends AbstractEntity {
    private int qteArticle;

    // Nav
    private Article article;
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
