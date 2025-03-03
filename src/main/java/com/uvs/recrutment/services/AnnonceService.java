package com.uvs.recrutment.services;

import com.uvs.recrutment.models.Annonce;
import com.uvs.recrutment.repositories.AnnonceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnnonceService {

    private final AnnonceRepository annonceRepository;

    public AnnonceService(AnnonceRepository annonceRepository) {
        this.annonceRepository = annonceRepository;
    }

    // Récupérer toutes les annonces
    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }

    // Récupérer une annonce par son ID
    public Optional<Annonce> getAnnonceById(Long id) {
        return annonceRepository.findById(id);
    }

    // Créer une nouvelle annonce
    public Annonce createAnnonce(Annonce annonce) {
        return annonceRepository.save(annonce);
    }

    // Mettre à jour une annonce
    public Optional<Annonce> updateAnnonce(Long id, Annonce annonceDetails) {
        return annonceRepository.findById(id).map(annonce -> {
            annonce.setTitre(annonceDetails.getTitre());
            annonce.setDescription(annonceDetails.getDescription());
            annonce.setLocalisation(annonceDetails.getLocalisation());
            annonce.setTypePoste(annonceDetails.getTypePoste());
            annonce.setAnneeAcademique(annonceDetails.getAnneeAcademique());
            return annonceRepository.save(annonce);
        });
    }

    // Supprimer une annonce
    public void deleteAnnonce(Long id) {
        annonceRepository.deleteById(id);
    }

    // Recherche d'annonces par type de poste
    public List<Annonce> getAnnoncesByTypePoste(String typePoste) {
        return annonceRepository.findByTypePoste(typePoste);
    }

    // Recherche d'annonces par niveau de qualification
    public List<Annonce> getAnnoncesByNiveauQualification(String niveauQualification) {
        return annonceRepository.findByNiveauQualification(niveauQualification);
    }

    // Recherche d'annonces dont la date de début est avant une certaine date
    public List<Annonce> getAnnoncesByDateDebutBefore(String date) {
        return annonceRepository.findByDateDebutBefore(date);
    }

    // Recherche d'annonces par année académique
    public List<Annonce> getAnnoncesByAnneeAcademiqueId(Long anneeAcademiqueId) {
        return annonceRepository.findByAnneeAcademiqueId(anneeAcademiqueId);
    }
}
