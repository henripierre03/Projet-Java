package com.ism.data.repository.implement;

import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.Paiement;
import com.ism.data.repository.IPaiementRepository;

public class PaiementRepository extends Repository<Paiement> implements IPaiementRepository {
    public PaiementRepository() {
        super(Paiement.class);
    }
}
