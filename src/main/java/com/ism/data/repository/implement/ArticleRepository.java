package com.ism.data.repository.implement;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.Article;
import com.ism.data.repository.IArticleRepository;

public class ArticleRepository extends Repository<Article> implements IArticleRepository {
    public ArticleRepository() {
        super(Article.class);
    }

    public List<Article> selectAll() {
        List<Article> dettes = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            String query = String.format("SELECT u FROM %s u", Article.class.getName());
            dettes = em.createQuery(query, Article.class).getResultList();
            selectAllDetails(dettes);
            selectAllDemandeArticles(dettes);
            return dettes;
        } catch (Exception e) {
            System.err.println("Échec de la récupération des données : " + e.getMessage());
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private List<Article> selectAllDetails(List<Article> articles) {
        for(Article a : articles) {
            a.getDetails().size();
        }
        return articles;
    }

    private List<Article> selectAllDemandeArticles(List<Article> articles) {
        for(Article a : articles) {
            a.getDemandeArticles().size();
        }
        return articles;
    }

    @Override
    public List<Article> selectAllAvailable() {
        return selectAll().stream()
                .filter(article -> article.getQteStock() != 0)
                .collect(Collectors.toList());
    }
}
