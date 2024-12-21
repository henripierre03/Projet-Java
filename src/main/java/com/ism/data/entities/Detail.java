package com.ism.data.entities;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Detail extends AbstractEntity {
    private int qte;
    private Double prixVente;

    // Nav
    private Article article;
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
