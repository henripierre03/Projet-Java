package com.ism.core.factory.implement;

import com.ism.core.factory.IFactoryService;
import com.ism.core.factory.IFactoryView;
import com.ism.controllers.IArticleView;
import com.ism.controllers.IClientView;
import com.ism.controllers.IDemandeDetteView;
import com.ism.controllers.IDetteView;
import com.ism.controllers.IPaiementView;
import com.ism.controllers.IUserView;
import com.ism.controllers.implement.ArticleView;
import com.ism.controllers.implement.ClientView;
import com.ism.controllers.implement.DemandeDetteView;
import com.ism.controllers.implement.DetteView;
import com.ism.controllers.implement.PaiementView;
import com.ism.controllers.implement.UserView;

public class FactoryView implements IFactoryView {
    private IArticleView articleView;
    private IClientView clientView;
    private IDemandeDetteView demandeDetteView;
    private IDetteView detteView;
    private IPaiementView paiementView;
    private IUserView userView;
    private IFactoryService factoryService;

    public FactoryView(IFactoryService factoryService) {
        this.factoryService = factoryService;
    }

    @Override
    public IArticleView getInstanceArticleView() {
        return articleView == null ? new ArticleView(factoryService.getInstanceArticleService()) : articleView;
    }

    @Override
    public IClientView getInstanceClientView() {
        return clientView == null ? new ClientView() : clientView;
    }

    @Override
    public IDemandeDetteView getInstanceDemandeDetteView() {
        return demandeDetteView == null ? new DemandeDetteView() : demandeDetteView;
    }

    @Override
    public IDetteView getInstanceDetteView() {
        return detteView == null ? new DetteView(factoryService.getInstanceDetteService()) : detteView;
    }

    @Override
    public IPaiementView getInstancePaiementView() {
        return paiementView == null ? new PaiementView() : paiementView;
    }

    @Override
    public IUserView getInstanceUserView() {
        if (userView == null) {
            userView = new UserView();
        }
        return userView;
    }
}
