package com.ism.services;

import java.util.List;

import com.ism.data.entities.DemandeArticle;
import com.ism.data.entities.User;

public interface IDemandeArticleService {
    DemandeArticle add(DemandeArticle value);
    List<DemandeArticle> findAll();
    DemandeArticle findBy(DemandeArticle demandeArticle);
    int length();
    List<DemandeArticle> findAllDemandeDette(User user);
}
