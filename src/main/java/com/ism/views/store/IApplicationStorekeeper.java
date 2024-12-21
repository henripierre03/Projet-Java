package com.ism.views.store;

import com.ism.services.IClientService;
import com.ism.views.IApplication;
import com.ism.views.IClientView;

public interface IApplicationStorekeeper extends IApplication {
    void subMenuClient(IClientService clientService, IClientView clientView);
}
