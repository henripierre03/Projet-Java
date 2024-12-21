package com.ism.services.implement;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

import com.ism.data.entities.DemandeDette;
import com.ism.data.repository.IDemandeDetteRepository;
import com.ism.services.IDemandeDetteService;

public class DemandeDetteService implements IDemandeDetteService {
    private IDemandeDetteRepository demandeDetteRepository;

    public DemandeDetteService(IDemandeDetteRepository demandeDetteRepository) {
        this.demandeDetteRepository = demandeDetteRepository;
    }

    @Override
    public boolean add(DemandeDette value) {
        try {
            return demandeDetteRepository.insert(value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<DemandeDette> findAll() {
        try {
            return demandeDetteRepository.selectAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public DemandeDette findBy(DemandeDette demandeDette) {
        for (DemandeDette dette : findAll()) {
            if (Objects.equals(dette.getId(), demandeDette.getId())) {
                return dette;
            }
        }
        return null;
    }

    @Override
    public DemandeDette findBy(List<DemandeDette> demandeDettes,DemandeDette demandeDette) {
        for (DemandeDette dette : demandeDettes) {
            if (Objects.equals(dette.getId(), demandeDette.getId())) {
                return dette;
            }
        }
        return null;
    }

    @Override
    public int length() {
        return demandeDetteRepository.size();
    }

    @Override
    public void update(List<DemandeDette> demandeDettes, DemandeDette updateDemande) {
        try {
            updateDemande.setUpdatedAt(LocalDateTime.now());
            demandeDetteRepository.update(updateDemande);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
