package com.uvs.recrutment.repositories;

import com.uvs.recrutment.models.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long> {

    // Trouver un candidat par son email (utilisé pour l'authentification)
    Optional<Candidat> findByEmail(String email);

    // Vérifier si un email est déjà utilisé
    boolean existsByEmail(String email);
}
