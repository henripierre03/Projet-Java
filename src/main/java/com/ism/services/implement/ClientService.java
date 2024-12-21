package com.ism.services.implement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ism.data.entities.Client;
import com.ism.data.repository.IClientRepository;
import com.ism.services.IClientService;

public class ClientService implements IClientService {
    private IClientRepository clientRepository;

    public ClientService(IClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client add(Client client) {
        return clientRepository.insert(client);
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.selectAll();
    }

    @Override
    public List<Client> findAllCustomerAvailable() {
        return clientRepository.selectAllCustomerAvailable();
    }

    @Override
    public int length() {
        return clientRepository.size();
    }

    @Override
    public Client findBy(Client client) {
        for (Client cl : findAll()) {
            if (cl.getId() == client.getId()) {
                return cl;
            }
            if (client.getUser() != null && cl.getUser() != null
                    && Objects.equals(cl.getUser().getId(), client.getUser().getId())) {
                return cl;
            }
            if (client.getTel() != null && cl.getTel().compareTo(client.getTel()) == 0) {
                return cl;
            }
        }
        return null;
    }

    @Override
    public Client findBy(List<Client> clients, Client client) {
        for (Client cl : clients) {
            if (cl.getId() == client.getId()) {
                return cl;
            }
            if (client.getUser() != null && cl.getUser() != null
                    && Objects.equals(cl.getUser().getId(), client.getUser().getId())) {
                return cl;
            }
            if (client.getTel() != null && cl.getTel().compareTo(client.getTel()) == 0) {
                return cl;
            }
            if (client.getSurname() != null && cl.getSurname().compareTo(client.getSurname()) == 0) {
                return cl;
            }
            if (client.getTel() != null && cl.getTel().compareTo(client.getTel()) == 0) {
                return cl;
            }
        }
        return null;
    }

    @Override
    public List<Client> getAllActifs() {
        return clientRepository.selectAllActifs();
    }

    @Override
    public List<Client> getAllActifsWithAccount() {
        return clientRepository.selectAllActifs()
                .stream()
                .filter(cl -> cl.getUser() != null)
                .collect(Collectors.toList());
    }

    @Override
    public void update(List<Client> clients, Client updateClient) {
        updateClient.setUpdatedAt(LocalDateTime.now());
        updateClient.updateCumulMontantDu();
        clientRepository.update(updateClient);
    }
}
