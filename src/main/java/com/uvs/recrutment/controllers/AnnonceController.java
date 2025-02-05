package com.uvs.recrutment.controllers;

import com.uvs.recrutment.models.Annonce;
import com.uvs.recrutment.repositories.AnnonceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/annonces")
public class AnnonceController {

    @Autowired
    private AnnonceRepository annonceRepository;

    // Récupérer toutes les annonces (accessible à tout le monde)
    @GetMapping
    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }

    // Ajouter une annonce (seul un ADMIN peut le faire)
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Annonce createAnnonce(@RequestBody Annonce annonce) {
        return annonceRepository.save(annonce);
    }

    // Supprimer une annonce (ADMIN uniquement)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteAnnonce(@PathVariable Long id) {
        annonceRepository.deleteById(id);
    }
}