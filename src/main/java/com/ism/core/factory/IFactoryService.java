package com.ism.core.factory;

import com.ism.services.IArticleService;
import com.ism.services.IClientService;
import com.ism.services.IDemandeArticleService;
import com.ism.services.IDemandeDetteService;
import com.ism.services.IDetailService;
import com.ism.services.IDetteService;
import com.ism.services.IPaiementService;
import com.ism.services.IUserService;

public interface IFactoryService {
    IArticleService getInstanceArticleService();
    IClientService getInstanceClientService();
    IDemandeArticleService getInstanceDemandeArticleService();
    IDemandeDetteService getInstanceDemandeDetteService();
    IDetailService getInstanceDetailService();
    IDetteService getInstanceDetteService();
    IPaiementService getInstancePaiementService();
    IUserService getInstanceUserService();
}
