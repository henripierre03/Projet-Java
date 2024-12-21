package com.ism.core.config.router;

import java.util.Scanner;

import com.ism.core.config.security.Connexion;
import com.ism.core.config.security.IConnexion;
import com.ism.core.factory.IFactory;
import com.ism.data.entities.User;
import com.ism.services.IArticleService;
import com.ism.services.IClientService;
import com.ism.services.IDemandeArticleService;
import com.ism.services.IDemandeDetteService;
import com.ism.services.IDetailService;
import com.ism.services.IDetteService;
import com.ism.services.IPaiementService;
import com.ism.services.IUserService;
import com.ism.views.IArticleView;
import com.ism.views.IClientView;
import com.ism.views.IDemandeDetteView;
import com.ism.views.IDetteView;
import com.ism.views.IPaiementView;
import com.ism.views.IUserView;
import com.ism.views.admin.IApplicationAdmin;
import com.ism.views.admin.implement.ApplicationAdmin;
import com.ism.views.client.IApplicationClient;
import com.ism.views.client.implement.ApplicationClient;
import com.ism.views.store.IApplicationStorekeeper;
import com.ism.views.store.implement.ApplicationStorekeeper;

public class Router implements IRouter {
    private final IArticleService articleService;
    private final IArticleView articleView;
    private final IClientService clientService;
    private final IClientView clientView;
    private final IDemandeDetteService demandeDetteService;
    private final IDemandeDetteView demandeDetteView;
    private final IDemandeArticleService demandeArticleService;
    private final IDetailService detailService;
    private final IDetteService detteService;
    private final IDetteView detteView;
    private final IPaiementService paiementService;
    private final IPaiementView paiementView;
    private final IUserService userService;
    private final IUserView userView;

    private final IApplicationAdmin appAdmin;
    private final IApplicationClient appClient;
    private final IApplicationStorekeeper appStorekeeper;
    private final Scanner scanner;
    private final IConnexion conn;

    public Router(IFactory factory, Scanner scanner) {
        this.articleService = factory.getFactoryService().getInstanceArticleService();
        this.articleView = factory.getFactoryView().getInstanceArticleView();
        this.clientService = factory.getFactoryService().getInstanceClientService();
        this.clientView = factory.getFactoryView().getInstanceClientView();
        this.demandeDetteService = factory.getFactoryService().getInstanceDemandeDetteService();
        this.demandeDetteView = factory.getFactoryView().getInstanceDemandeDetteView();
        this.demandeArticleService = factory.getFactoryService().getInstanceDemandeArticleService();
        this.detailService = factory.getFactoryService().getInstanceDetailService();
        this.detteService = factory.getFactoryService().getInstanceDetteService();
        this.detteView = factory.getFactoryView().getInstanceDetteView();
        this.paiementService = factory.getFactoryService().getInstancePaiementService();
        this.paiementView = factory.getFactoryView().getInstancePaiementView();
        this.userService = factory.getFactoryService().getInstanceUserService();
        this.userView = factory.getFactoryView().getInstanceUserView();
        this.scanner = scanner;

        this.appAdmin = new ApplicationAdmin(this.articleService, this.articleView, this.clientService, this.clientView, this.detteService, this.detteView, this.userService, this.userView, this.scanner);
        this.appClient = new ApplicationClient(this.articleService, clientService, this.demandeDetteService, this.demandeDetteView, this.demandeArticleService, this.detteService, this.detteView, this.scanner);
        this.appStorekeeper = new ApplicationStorekeeper(this.articleService, this.clientService, this.clientView, this.demandeDetteService, this.demandeDetteView, this.detailService, this.detteService, this.detteView, this.paiementService, this.paiementView, userService, userView, this.scanner);
        this.conn = new Connexion(this.userService, this.scanner);
    }

    @Override
    public void navigate() {
        User user;
        do {
            user = conn.connexion();
            switch (user.getRole().name()) {
                case "ADMIN":
                    appAdmin.run(user); 
                    break;
                case "CLIENT":
                    appClient.run(user);
                    break;
                case "BOUTIQUIER":
                    appStorekeeper.run(user);
                    break;
                default:
                    break;
            }
        } while (user != null);
    }
}
