package com.ism.core.factory.implement;

import com.ism.core.factory.IFactoryRepository;
import com.ism.core.factory.IFactoryService;
import com.ism.services.IArticleService;
import com.ism.services.IClientService;
import com.ism.services.IDemandeArticleService;
import com.ism.services.IDemandeDetteService;
import com.ism.services.IDetailService;
import com.ism.services.IDetteService;
import com.ism.services.IPaiementService;
import com.ism.services.IUserService;
import com.ism.services.implement.ArticleService;
import com.ism.services.implement.ClientService;
import com.ism.services.implement.DemandeArticleService;
import com.ism.services.implement.DemandeDetteService;
import com.ism.services.implement.DetailService;
import com.ism.services.implement.DetteService;
import com.ism.services.implement.PaiementService;
import com.ism.services.implement.UserService;

public class FactoryService implements IFactoryService {
    private IArticleService articleService;
    private IClientService clientService;
    private IDemandeArticleService demandeArticleService;
    private IDemandeDetteService demandeDetteService;
    private IDetailService detailService;
    private IDetteService detteService;
    private IPaiementService paiementService;
    private IUserService userService;
    private IFactoryRepository factoryRepository;

    public FactoryService(IFactoryRepository factoryRepository) {
        this.factoryRepository = factoryRepository;
    }

    @Override
    public IArticleService getInstanceArticleService() {
        return articleService == null ? new ArticleService(factoryRepository.getInstanceArticleRepository()) : articleService;
    }

    @Override
    public IClientService getInstanceClientService() {
        return clientService == null ? new ClientService(factoryRepository.getInstanceClientRepository()) : clientService;
    }

    @Override
    public IDemandeArticleService getInstanceDemandeArticleService() {
        return demandeArticleService == null ? new DemandeArticleService(factoryRepository.getInstanceDemandeArticleRepository()) : demandeArticleService;
    }

    @Override
    public IDemandeDetteService getInstanceDemandeDetteService() {
        return demandeDetteService == null ? new DemandeDetteService(factoryRepository.getInstanceDemandeDetteRepository()) : demandeDetteService;
    }

    @Override
    public IDetailService getInstanceDetailService() {
        return detailService == null ? new DetailService(factoryRepository.getInstanceDetailRepository()) : detailService;
    }

    @Override
    public IDetteService getInstanceDetteService() {
        return detteService == null ? new DetteService(factoryRepository.getInstanceDetteRepository()) : detteService;
    }

    @Override
    public IPaiementService getInstancePaiementService() {
        return paiementService == null? new PaiementService(factoryRepository.getInstancePaiementRepository()) : paiementService;
    }

    @Override
    public IUserService getInstanceUserService() {
        return userService == null ? new UserService(factoryRepository.getInstanceUserRepository()) : userService;
    }
}
