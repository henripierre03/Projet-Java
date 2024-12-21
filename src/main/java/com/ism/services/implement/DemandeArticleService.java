package com.ism.services.implement;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ism.data.entities.DemandeArticle;
import com.ism.data.entities.User;
import com.ism.data.repository.IDemandeArticleRepository;
import com.ism.services.IDemandeArticleService;

public class DemandeArticleService implements IDemandeArticleService {
    private IDemandeArticleRepository demandeArticleRepository;

    public DemandeArticleService(IDemandeArticleRepository demandeArticleRepository) {
        this.demandeArticleRepository = demandeArticleRepository;
    }

    @Override
    public DemandeArticle add(DemandeArticle value) {
        return demandeArticleRepository.insert(value);
    }

    @Override
    public List<DemandeArticle> findAll() {
        return demandeArticleRepository.selectAll();
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

    public List<DemandeArticle> findAllDemandeDette(User user) {
        return demandeArticleRepository.selectAll()
                .stream()
                .filter(article -> user != null && user.getId() == article.getDemandeDette().getClient().getUser().getId())
                .collect(Collectors.toList());
    }

    @Override
    public int length() {
        return demandeArticleRepository.size();
    }
    
}
