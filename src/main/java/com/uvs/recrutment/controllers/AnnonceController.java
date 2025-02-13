package com.uvs.recrutment.controllers;

import com.uvs.recrutment.models.Annonce;
import com.uvs.recrutment.repositories.AnnonceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annonces")
@CrossOrigin(origins = "http://localhost:4200") // Autorise Angular à accéder à l'API
public class AnnonceController {

    @Autowired
    private AnnonceRepository annonceRepository;

    // Récupérer toutes les annonces (accessible à tout le monde)
    @GetMapping
    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }

    // Récupérer une annonce par ID
    @GetMapping("/{id}")
    public Annonce getAnnonceById(@PathVariable Long id) {
        return annonceRepository.findById(id).orElse(null);
    }

    // Ajouter une annonce (seul un ADMIN peut le faire)
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Annonce createAnnonce(@RequestBody Annonce annonce) {
        return annonceRepository.save(annonce);
    }

    // Mettre à jour une annonce (ADMIN uniquement)
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Annonce updateAnnonce(@PathVariable Long id, @RequestBody Annonce annonceDetails) {
        return annonceRepository.findById(id).map(annonce -> {
            annonce.setTitre(annonceDetails.getTitre());
            annonce.setDescription(annonceDetails.getDescription());
            annonce.setLocalisation(annonceDetails.getLocalisation());
            annonce.setTypePoste(annonceDetails.getTypePoste());
            return annonceRepository.save(annonce);
        }).orElse(null);
    }

    // Supprimer une annonce (ADMIN uniquement)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteAnnonce(@PathVariable Long id) {
        annonceRepository.deleteById(id);
    }
}
