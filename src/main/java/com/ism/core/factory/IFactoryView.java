package com.ism.core.factory;

import com.ism.controllers.IArticleView;
import com.ism.controllers.IClientView;
import com.ism.controllers.IDemandeDetteView;
import com.ism.controllers.IDetteView;
import com.ism.controllers.IPaiementView;
import com.ism.controllers.IUserView;

public interface IFactoryView {
    IArticleView getInstanceArticleView();
    IClientView getInstanceClientView();
    IDemandeDetteView getInstanceDemandeDetteView();
    IUserView getInstanceUserView();
    IPaiementView getInstancePaiementView();
    IDetteView getInstanceDetteView();
}
