package com.uvs.recrutment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.uvs.recrutment.models.Candidat;
import com.uvs.recrutment.repositories.CandidatRepository;

@Service
public class CandidatService {

    @Autowired
    private CandidatRepository candidatRepository;

    // Récupérer un candidat par son email
    public Optional<Candidat> getCandidatByEmail(String email) {
        return candidatRepository.findByEmail(email);
    }

    // Vérifier si un email existe déjà avant inscription
    public boolean emailExiste(String email) {
        return candidatRepository.existsByEmail(email);
    }

    // Récupérer tous les candidats
    public List<Candidat> getAllCandidats() {
        return candidatRepository.findAll();
    }
}
