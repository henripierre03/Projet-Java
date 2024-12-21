package com.ism.services.implement;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ism.data.entities.Client;
import com.ism.data.entities.Dette;
import com.ism.data.enums.EtatDette;
import com.ism.data.repository.IDetteRepository;
import com.ism.services.IDetteService;

public class DetteService implements IDetteService {
    private IDetteRepository detteRepository;

    public DetteService(IDetteRepository detteRepository) {
        this.detteRepository = detteRepository;
    }

    @Override
    public Dette add(Dette value) {
        return detteRepository.insert(value);
    }

    @Override
    public List<Dette> findAll() {
        return detteRepository.selectAll();
    }

    @Override
    public int length() {
        return detteRepository.size();
    }

    @Override
    public Dette findBy(Dette dette) {
        return detteRepository.selectBy(dette.getId());
    }

    @Override
    public List<Dette> findAllBy(Client client) {
        return getAllNonSoldes()
                .stream()
                .filter(d -> (client != null && d.getClient().getId() == client.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Dette> findAllDetteNonSoldeForClient(Client client) {
        return getAllNonSoldes()
                .stream()
                .filter(d -> d.getClient().getUser() != null && d.getClient().getUser().getId() == client.getUser().getId() && d.getEtat() == EtatDette.ENCOURS)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dette> findAllDetteForClient(Client client) {
        return getAllNonSoldes()
                .stream()
                .filter(d -> d.getClient().getUser() != null && d.getClient().getUser().getId() == client.getUser().getId())
                .collect(Collectors.toList());
    }

    @Override
    public Dette findBy(List<Dette> dettes, Dette dette) {
        for (Dette d : dettes) {
            if (Objects.equals(d.getId(), dette.getId())) {
                return d;
            } else if (d.getClient().getUser().getId() == dette.getClient().getUser().getId()) {
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
        detteRepository.update(updatedDette);
    }
}
