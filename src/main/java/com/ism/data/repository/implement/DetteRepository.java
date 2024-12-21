package com.ism.data.repository.implement;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.Dette;
import com.ism.data.repository.IDetteRepository;

public class DetteRepository extends Repository<Dette> implements IDetteRepository {
    public DetteRepository() {
        super(Dette.class);
    }

    public List<Dette> selectAll() {
        List<Dette> dettes = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            String query = String.format("SELECT u FROM %s u", Dette.class.getName());
            dettes = em.createQuery(query, Dette.class).getResultList();
            selectAllPay(dettes);
            selectAllDetails(dettes);
            return dettes;
        } catch (Exception e) {
            System.err.println("Échec de la récupération des données : " + e.getMessage());
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Dette> selectAllPay(List<Dette> dettes) {
        for (Dette d : dettes) {
            d.getPaiements().size();
        }
        return dettes;
    }

    public List<Dette> selectAllDetails(List<Dette> dettes) {
        for (Dette d : dettes) {
            d.getDetails().size();
        }
        return dettes;
    }

    @Override
    public List<Dette> selectAllSoldes() {
        return selectAll().stream()
                .filter(dette -> dette.getMontantRestant() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dette> selectAllNonSoldes() {
        return selectAll().stream()
                .filter(dette -> (dette.getClient() != null && dette.getClient().getCumulMontantDu() != 0.0))
                .collect(Collectors.toList());
    }

}