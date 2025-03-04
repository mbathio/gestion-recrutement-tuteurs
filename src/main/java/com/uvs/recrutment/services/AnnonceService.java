package com.uvs.recrutment.services;

import com.uvs.recrutment.models.Annonce;
import com.uvs.recrutment.models.Candidature;
import com.uvs.recrutment.models.AnneeAcademique;
import com.uvs.recrutment.repositories.AnnonceRepository;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnnonceService {

    @Autowired
    private AnnonceRepository annonceRepository;

    @OneToMany(mappedBy = "annonce", cascade = CascadeType.REMOVE)
private List<Candidature> candidatures;

    public Annonce createAnnonce(String titre, String description, LocalDate dateDebut, LocalDate dateFin, String statut, AnneeAcademique anneeAcademique) {
        Annonce annonce = new Annonce();
        annonce.setTitre(titre);
        annonce.setDescription(description);
        annonce.setDateDebut(dateDebut);
        annonce.setDateFin(dateFin);
        annonce.setStatut(statut);
        annonce.setAnneeAcademique(anneeAcademique);
        return annonceRepository.save(annonce);
    }

    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }

    public Annonce updateAnnonce(Long id, String titre, String description, LocalDate dateDebut, LocalDate dateFin, String statut) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));
        annonce.setTitre(titre);
        annonce.setDescription(description);
        annonce.setDateDebut(dateDebut);
        annonce.setDateFin(dateFin);
        annonce.setStatut(statut);
        return annonceRepository.save(annonce);
    }

    public void deleteAnnonce(Long id) {
        if (!annonceRepository.existsById(id)) {
            throw new RuntimeException("Annonce non trouvée");
        }
        annonceRepository.deleteById(id);
    }
}
