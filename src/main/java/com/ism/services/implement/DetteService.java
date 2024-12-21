package com.ism.services.implement;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

import com.ism.data.entities.Dette;
import com.ism.data.repository.IDetteRepository;
import com.ism.services.IDetteService;

public class DetteService implements IDetteService {
    private IDetteRepository detteRepository;

    public DetteService(IDetteRepository detteRepository) {
        this.detteRepository = detteRepository;
    }

    @Override
    public boolean add(Dette value) {
        try {
            return detteRepository.insert(value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Dette> findAll() {
        try {
            return detteRepository.selectAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public int length() {
        return detteRepository.size();
    }

    @Override
    public Dette findBy(Dette dette) {
        try {
            return detteRepository.selectBy(dette.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Dette findBy(List<Dette> dettes, Dette dette) {
        for (Dette d : dettes) {
            if (Objects.equals(d.getId(), dette.getId())) {
                return d;
            }
        }
        return null;
    }

    @Override
    public List<Dette> getAllSoldes() {
        return detteRepository.selectAllSoldes();
    }

    @Override
    public List<Dette> getAllNonSoldes() {
        return detteRepository.selectAllNonSoldes();
    }

    @Override
    public void update(Dette updatedDette) {
        try {
            detteRepository.update(updatedDette);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
