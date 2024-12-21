package com.ism.services;

import java.util.List;

import com.ism.data.entities.Paiement;

public interface IPaiementService {
    boolean add(Paiement value);
    List<Paiement> findAll();
    Paiement findBy(List<Paiement> paiements, Paiement paiement);
    int length();
}
