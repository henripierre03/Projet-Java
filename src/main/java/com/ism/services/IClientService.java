package com.ism.services;

import java.util.List;

import com.ism.data.entities.Client;

public interface IClientService {
    boolean add(Client value);
    List<Client> findAll();
    int length();
    Client findBy(Client client);
    Client findBy(List<Client> clients, Client client);
    List<Client> getAllActifs();
    List<Client> findAllCustomerAvailable();
    void update(List<Client> clients, Client updateClient);
}
