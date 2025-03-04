package com.uvs.recrutment.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.uvs.recrutment.models.AnneeAcademique;
import com.uvs.recrutment.models.Annonce;
import com.uvs.recrutment.services.AnnonceService;
import com.uvs.recrutment.services.AnneeAcademiqueService;
import com.uvs.recrutment.dto.AnnonceRequest;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import java.util.Date;

@RestController
@RequestMapping("/api/admin/annonces")
public class AnnonceController {

    @Autowired
    private AnnonceService annonceService;

    @Autowired
    private AnneeAcademiqueService anneeAcademiqueService;

    @PostMapping
    public ResponseEntity<Annonce> createAnnonce(@Valid @RequestBody AnnonceRequest request) {
        // Try to get the current academic year or create a new one if not exists
        AnneeAcademique anneeAcademique = anneeAcademiqueService.getAllAnnees().stream()
            .findFirst()
            .orElseGet(() -> {
                AnneeAcademique newAnnee = new AnneeAcademique();
                newAnnee.setLibelle(java.time.Year.now() + "-" + (java.time.Year.now().getValue() + 1));
                newAnnee.setDateDebut(new Date());
                newAnnee.setDateFin(new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000)); // One year from now
                return anneeAcademiqueService.createAnnee(newAnnee);
            });

        Annonce annonce = annonceService.createAnnonce(
            request.getTitre(), request.getDescription(), request.getDateDebut(), request.getDateFin(), request.getStatut(), anneeAcademique
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(annonce);
    }

    @GetMapping
    public List<Annonce> getAllAnnonces() {
        return annonceService.getAllAnnonces();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Annonce> updateAnnonce(@PathVariable Long id, @Valid @RequestBody AnnonceRequest request) {
        Annonce annonce = annonceService.updateAnnonce(
            id, request.getTitre(), request.getDescription(), request.getDateDebut(), request.getDateFin(), request.getStatut()
        );
        return ResponseEntity.ok(annonce);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnonce(@PathVariable Long id) {
        annonceService.deleteAnnonce(id);
        return ResponseEntity.noContent().build();
    }
}
