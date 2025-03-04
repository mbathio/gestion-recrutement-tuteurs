package com.uvs.recrutment.services;

import com.uvs.recrutment.models.Candidature;
import com.uvs.recrutment.repositories.CandidatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidatureService {

    @Autowired
    private CandidatureRepository candidatureRepository;

    public Candidature createCandidature(Candidature candidature) {
        return candidatureRepository.save(candidature);
    }

    public Optional<Candidature> getCandidatureById(Long id) {
        return candidatureRepository.findById(id);
    }

    public Candidature updateCandidature(Candidature candidature) {
        return candidatureRepository.save(candidature);
    }

    public boolean deleteCandidature(Long id) {
        if (candidatureRepository.existsById(id)) {
            candidatureRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Candidature> getAllCandidatures() {
        return candidatureRepository.findAll();
    }
}
