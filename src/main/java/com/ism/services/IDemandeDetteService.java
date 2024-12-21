package com.ism.services;

import java.util.List;

import com.ism.data.entities.Client;
import com.ism.data.entities.DemandeDette;

public interface IDemandeDetteService{
    DemandeDette add(DemandeDette value);
    List<DemandeDette> findAll();
    DemandeDette findBy(DemandeDette demandeDette);
    int length();
    List<DemandeDette> findAllByState(String state);
    void update(List<DemandeDette> demandeDettes, DemandeDette updateDemande);
    DemandeDette findBy(List<DemandeDette> demandeDettes,DemandeDette demandeDette);
    List<DemandeDette> findAllDemandeDettesForClient(Client client);
}
