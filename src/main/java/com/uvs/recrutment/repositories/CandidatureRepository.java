package com.uvs.recrutment.repositories;

import com.uvs.recrutment.models.Candidature;
import com.uvs.recrutment.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    // Recherche des candidatures par utilisateur (candidat)
    List<Candidature> findByCandidat(User candidat);
}
