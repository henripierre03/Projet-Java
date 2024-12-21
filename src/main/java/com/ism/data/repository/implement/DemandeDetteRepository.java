package com.ism.data.repository.implement;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.DemandeDette;
import com.ism.data.repository.IDemandeDetteRepository;

public class DemandeDetteRepository extends Repository<DemandeDette> implements IDemandeDetteRepository {
    public DemandeDetteRepository() {
        super(DemandeDette.class);
    }

    public List<DemandeDette> selectAll() {
        List<DemandeDette> dettes = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            String query = String.format("SELECT u FROM %s u", DemandeDette.class.getName());
            dettes = em.createQuery(query, DemandeDette.class).getResultList();
            selectAllDemandeArticle(dettes);
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

    private List<DemandeDette> selectAllDemandeArticle(List<DemandeDette> dettes) {
        for(DemandeDette d : dettes) {
            d.getDemandeArticles().size();
        }
        return dettes;
    }
}
