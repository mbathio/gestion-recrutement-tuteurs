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

    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }

    public Optional<Annonce> getAnnonceById(Long id) {
        return annonceRepository.findById(id);
    }

    public Annonce createAnnonce(Annonce annonce) {
        return annonceRepository.save(annonce);
    }

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

    public void deleteAnnonce(Long id) {
        annonceRepository.deleteById(id);
    }
}
