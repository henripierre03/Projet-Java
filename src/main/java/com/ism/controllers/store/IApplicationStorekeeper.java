package com.ism.controllers.store;

import com.ism.services.IClientService;
import com.ism.controllers.IApplication;
import com.ism.controllers.IClientView;

public interface IApplicationStorekeeper extends IApplication {
    void subMenuClient(IClientService clientService, IClientView clientView);
}
