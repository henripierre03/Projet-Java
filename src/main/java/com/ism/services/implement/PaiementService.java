package com.ism.services.implement;

import java.util.List;
import java.util.Objects;

import com.ism.data.entities.Paiement;
import com.ism.data.repository.IPaiementRepository;
import com.ism.services.IPaiementService;

public class PaiementService implements IPaiementService {
    private IPaiementRepository paiementRepository;

    public PaiementService(IPaiementRepository paiementRepository) {
        this.paiementRepository = paiementRepository;
    }

    @Override
    public Paiement add(Paiement value) {
        return paiementRepository.insert(value);
    }

    @Override
    public List<Paiement> findAll() {
        return paiementRepository.selectAll();
    }

    @Override
    public Paiement findBy(List<Paiement> paiements, Paiement paiement) {
        for (Paiement pay : paiements) {
            if (Objects.equals(pay.getId(), paiement.getId())) {
                return pay;
            }
        }
        return null;
    }

    @Override
    public int length() {
        return paiementRepository.size();
    }
    
}
