package com.ism.services;

import java.util.List;

import com.ism.data.entities.DemandeArticle;

public interface IDemandeArticleService {
    boolean add(DemandeArticle value);
    List<DemandeArticle> findAll();
    DemandeArticle findBy(DemandeArticle demandeArticle);
    int length();
}
