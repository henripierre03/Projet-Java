package com.ism.services;

import java.util.List;

import com.ism.data.entities.DemandeDette;

public interface IDemandeDetteService{
    boolean add(DemandeDette value);
    List<DemandeDette> findAll();
    DemandeDette findBy(DemandeDette demandeDette);
    int length();
    void update(List<DemandeDette> demandeDettes, DemandeDette updateDemande);
    DemandeDette findBy(List<DemandeDette> demandeDettes,DemandeDette demandeDette);
}
