package com.ism.views.client;

import com.ism.data.entities.Dette;
import com.ism.data.entities.User;
import com.ism.services.IArticleService;
import com.ism.services.IClientService;
import com.ism.services.IDemandeArticleService;
import com.ism.services.IDemandeDetteService;
import com.ism.services.IDetteService;
import com.ism.views.IApplication;
import com.ism.views.IDemandeDetteView;
import com.ism.views.IDetteView;

public interface IApplicationClient extends IApplication {
    void displayDette(IDetteService detteService, IDetteView detteView);
    void displayPaiement(Dette dette);
    void displayArticle(Dette dette);
    void subMenu(Dette dette);
    void saisirDette(IArticleService articleService, IClientService clientService,IDemandeDetteService demandeDetteService, IDemandeDetteView demandeDetteView, IDemandeArticleService demandeArticleService, User user);
    void displayDemandeDette(IDemandeDetteService demandeDetteService, IDemandeDetteView demandeDetteView);
    void subMenuDemandeDette(IDemandeDetteService demandeDetteService, IDemandeDetteView demandeDetteView);
    void relaunchDette(IDemandeDetteService demandeDetteService, IDemandeDetteView demandeDetteView);
}
