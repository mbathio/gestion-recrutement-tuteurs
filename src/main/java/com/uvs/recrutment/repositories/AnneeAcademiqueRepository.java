package com.uvs.recrutment.repositories;

import com.uvs.recrutment.models.AnneeAcademique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnneeAcademiqueRepository extends JpaRepository<AnneeAcademique, Long> {
    Optional<AnneeAcademique> findByAnnee(String annee); // Méthode pour trouver par 'annee'
}
