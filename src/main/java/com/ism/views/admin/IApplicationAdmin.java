package com.ism.views.admin;

import com.ism.data.entities.User;
import com.ism.services.IArticleService;
import com.ism.services.IClientService;
import com.ism.services.IDetteService;
import com.ism.services.IUserService;
import com.ism.views.IApplication;
import com.ism.views.IArticleView;
import com.ism.views.IClientView;
import com.ism.views.IDetteView;
import com.ism.views.IUserView;

public interface IApplicationAdmin extends IApplication {
    int role();
    int status();
    void msgStatus(boolean state);
    void soldes(IDetteService detteService, IDetteView detteView);
    void updateQte(IArticleService articleService, IArticleView articleView);
    void listingArticleAvailable(IArticleService articleService, IArticleView articleView);
    void createArticle(IArticleService articleService, IArticleView articleView);
    void listingUserActifs(IUserService userService, IUserView userView, User userConnect);
    void activeDesactiveAccount(IUserService userService, IUserView userView, User userConnect);
    void createAccountCustomer(IClientService clientService, IClientView clientView, IUserService userService, IUserView userView);
}
