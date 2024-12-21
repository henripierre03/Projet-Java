package com.ism.services.implement;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

import com.ism.data.entities.DemandeArticle;
import com.ism.data.repository.IDemandeArticleRepository;
import com.ism.services.IDemandeArticleService;

public class DemandeArticleService implements IDemandeArticleService {
    private IDemandeArticleRepository demandeArticleRepository;

    public DemandeArticleService(IDemandeArticleRepository demandeArticleRepository) {
        this.demandeArticleRepository = demandeArticleRepository;
    }

    @Override
    public boolean add(DemandeArticle value) {
        try {
            return demandeArticleRepository.insert(value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<DemandeArticle> findAll() {
        try {
            return demandeArticleRepository.selectAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public DemandeArticle findBy(DemandeArticle demandeArticle) {
        for (DemandeArticle article : findAll()) {
            if (Objects.equals(article.getId(), demandeArticle.getId())) {
                return article;
            }
        }
        return null;
    }

    @Override
    public int length() {
        return demandeArticleRepository.size();
    }
    
}
