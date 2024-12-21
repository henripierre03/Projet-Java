package com.ism.services;

import java.util.List;

import com.ism.data.entities.Article;

public interface IArticleService {
    boolean add(Article value);
    List<Article> findAll();
    void update(Article article);
    List<Article> findAllAvailable();
    Article findBy(Article article, List<Article> articles);
    int length();
}
