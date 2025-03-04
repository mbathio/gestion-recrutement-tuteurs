package com.uvs.recrutment.services;

import com.uvs.recrutment.models.Administrateur;
import com.uvs.recrutment.repositories.AdministrateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministrateurService {

    @Autowired
    private AdministrateurRepository administrateurRepository;

    // Récupère tous les administrateurs
    public List<Administrateur> getAllAdministrateurs() {
        return administrateurRepository.findAll();
    }

    // Récupère un administrateur par son ID
    public Administrateur getAdministrateurById(Long id) {
        return administrateurRepository.findById(id).orElse(null);
    }
}
