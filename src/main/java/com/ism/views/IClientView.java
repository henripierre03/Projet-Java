package com.ism.views;

import java.util.List;

import com.ism.data.entities.Client;
import com.ism.services.IClientService;

public interface IClientView extends IView<Client> {
    void display(List<Client> clients);
    void displayClient(Client client);
    Client saisir(IClientService clientService);
}
