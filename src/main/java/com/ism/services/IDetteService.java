package com.ism.services;

import java.util.List;

import com.ism.data.entities.Client;
import com.ism.data.entities.Dette;

public interface IDetteService {
    Dette add(Dette value);
    List<Dette> findAll();
    int length();
    Dette findBy(Dette dette);
    Dette findBy(List<Dette> dettes, Dette dette);
    List<Dette> getAllSoldes();
    List<Dette> getAllNonSoldes();
    void update(Dette dette);
    List<Dette> findAllBy(Client client);
    List<Dette> findAllDetteForClient(Client client);
    List<Dette> findAllDetteNonSoldeForClient(Client client);
}
