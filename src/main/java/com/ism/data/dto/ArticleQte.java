package com.ism.data.dto;

import com.ism.data.entities.Article;

public record ArticleQte(Article article, int qte) {
    // Add your custom logic here
    public ArticleQte {
        if (article == null || qte <= 0) {
            throw new IllegalArgumentException("Article and quantity must be non-null and positive");
        }
    }
}
