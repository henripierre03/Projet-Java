package com.ism.data.repository.implement;

import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.DemandeArticle;
import com.ism.data.repository.IDemandeArticleRepository;

public class DemandeArticleRepository extends Repository<DemandeArticle> implements IDemandeArticleRepository {
    public DemandeArticleRepository() {
        super(DemandeArticle.class);
    }
}
