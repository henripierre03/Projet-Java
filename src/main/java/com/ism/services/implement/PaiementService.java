package com.ism.services.implement;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

import com.ism.data.entities.Paiement;
import com.ism.data.repository.IPaiementRepository;
import com.ism.services.IPaiementService;

public class PaiementService implements IPaiementService {
    private IPaiementRepository paiementRepository;

    public PaiementService(IPaiementRepository paiementRepository) {
        this.paiementRepository = paiementRepository;
    }

    @Override
    public boolean add(Paiement value) {
        try {
            return paiementRepository.insert(value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Paiement> findAll() {
        try {
            return paiementRepository.selectAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
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
