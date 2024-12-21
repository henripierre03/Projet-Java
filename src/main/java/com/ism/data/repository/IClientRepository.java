package com.ism.data.repository;

import java.util.List;

import com.ism.core.repository.IRepository;
import com.ism.data.entities.Client;

public interface IClientRepository extends IRepository<Client> {
    List<Client> selectAllActifs();
    List<Client> selectAllCustomerAvailable();
}
