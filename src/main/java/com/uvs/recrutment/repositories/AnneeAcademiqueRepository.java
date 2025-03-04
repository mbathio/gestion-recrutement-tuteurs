package com.uvs.recrutment.repositories;

import com.uvs.recrutment.models.AnneeAcademique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnneeAcademiqueRepository extends JpaRepository<AnneeAcademique, Long> {
    boolean existsByLibelle(String libelle);
}
