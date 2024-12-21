package com.ism.data.repository.implement;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.Client;
import com.ism.data.repository.IClientRepository;

public class ClientRepository extends Repository<Client> implements IClientRepository {
    public ClientRepository() {
        super(Client.class);
    }

    @Override
    public List<Client> selectAll() {
        EntityManager em = getEntityManager();
        String sql = "SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.dettes";
        return em.createQuery(sql, Client.class).getResultList();
    }

    @Override
    public List<Client> selectAllActifs() {
        return selectAll().stream()
                .filter(Client::isStatus)
                .collect(Collectors.toList());
    }

    @Override
    public List<Client> selectAllCustomerAvailable() {
        return selectAll().stream()
                .filter(cl -> cl.getUser() == null)
                .collect(Collectors.toList());
    }

}
