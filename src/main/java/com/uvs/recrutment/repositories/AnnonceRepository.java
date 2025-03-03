package com.uvs.recrutment.repositories;

import com.uvs.recrutment.models.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long> {
    
    // Recherche d'annonces par type de poste
    List<Annonce> findByTypePoste(String typePoste);
    
    // Recherche d'annonces par niveau de qualification
    List<Annonce> findByNiveauQualification(String niveauQualification);
    
    // Recherche d'annonces par date de début avant une certaine date
    List<Annonce> findByDateDebutBefore(String date);
    
    // Recherche d'annonces par année académique
    List<Annonce> findByAnneeAcademiqueId(Long anneeAcademiqueId);
}
